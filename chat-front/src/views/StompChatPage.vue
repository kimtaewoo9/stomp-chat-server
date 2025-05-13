<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" md="8">
                <v-card flat class="chat-card">
                    <v-card-text class="pa-0">
                        <div class="chat-box" ref="chatBox">
                            <template v-for="(msg, index) in messages" :key="msg.id || index">
                                <div v-if="shouldShowDateSeparator(index)" class="date-separator">
                                    <v-icon size="small" class="mr-1">mdi-calendar-blank-outline</v-icon>
                                    {{ formatDate(msg.createdAt) }}
                                </div>

                                <div
                                    :class="['chat-message', msg.senderEmail === senderEmail ? 'sent' : 'received']"
                                >
                                    <div
                                        class="message-header"
                                        :class="{
                                            'text-right': msg.senderEmail === senderEmail,
                                            'text-left': msg.senderEmail !== senderEmail
                                        }"
                                    >
                                        <span class="message-time ml-2">{{ formatTime(msg.createdAt) }}</span>
                                    </div>
                                    <div
                                        class="message-content"
                                        :class="{
                                            'text-right': msg.senderEmail === senderEmail,
                                            'text-left': msg.senderEmail !== senderEmail
                                        }"
                                    >
                                        <strong>{{ msg.senderName }}: </strong>{{ msg.message }}
                                    </div>
                                </div>
                            </template>
                        </div>
                        <div class="input-area pa-2">
                            <div class="d-flex align-center">
                                <v-text-field
                                    v-model="newMessage"
                                    placeholder="메시지 입력.."
                                    variant="outlined"
                                    density="compact"
                                    hide-details
                                    class="message-input mr-2"
                                    @keyup.enter="sendMessage"
                                />
                                <v-btn
                                    class="retro-button"
                                    @click="sendMessage"
                                >
                                    전송
                                </v-btn>
                            </div>
                        </div>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';
import axios from 'axios';

export default {
    data() {
        return {
            messages: [],
            newMessage: "",
            stompClient: null,
            token: "",
            senderEmail: null,
            senderName: null,
            roomId: null
        }
    },
    async created() {
        this.senderEmail = localStorage.getItem("email");
        this.senderName = localStorage.getItem("name") || "사용자";
        this.roomId = this.$route.params.roomId;
        try {
            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/chat/history/${this.roomId}`);
            this.messages = response.data.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
            this.connectWebsocket();
            this.$nextTick(() => {
                this.scrollToBottom();
            });
        } catch (error) {
            console.error("Error fetching chat history:", error);
        }
    },
    beforeRouteLeave(to, from, next) {
        this.disconnectWebSocket();
        next();
    },
    beforeUnmount() {
        this.disconnectWebSocket();
    },
    methods: {
        connectWebsocket() {
            if (this.stompClient && this.stompClient.connected) return;
            const sockJs = new SockJS(`${process.env.VUE_APP_API_BASE_URL}/connect`);
            this.stompClient = Stomp.over(sockJs);
            this.token = localStorage.getItem("token");
            this.stompClient.connect(
                {
                    Authorization: `Bearer ${this.token}`
                },
                () => {
                    this.stompClient.subscribe(
                        `/topic/${this.roomId}`,
                        (message) => {
                            try {
                                const parseMessage = JSON.parse(message.body);
                                if(!parseMessage.createdAt) {
                                    parseMessage.createdAt = new Date().toISOString();
                                }
                                this.messages.push(parseMessage);
                                this.scrollToBottom();
                            } catch(e) {
                                console.error("Failed to parse incoming message:", message.body, e);
                            }
                        },
                        {
                            Authorization: `Bearer ${this.token}`
                        }
                    );
                },
                (error) => {
                    console.error("WebSocket connection error:", error);
                }
            );
        },
        sendMessage() {
            if (!this.newMessage.trim()) return;
            if (!this.stompClient || !this.stompClient.connected) {
                 console.error("WebSocket is not connected.");
                 return;
            }

            const message = {
                senderEmail: this.senderEmail,
                senderName: this.senderName,
                message: this.newMessage,
                createdAt: new Date().toISOString()
            };
            try {
                this.stompClient.send(`/publish/${this.roomId}`, JSON.stringify(message), {
                    Authorization: `Bearer ${this.token}`
                });
                this.newMessage = "";
            } catch (error) {
                console.error("Failed to send message:", error);
            }
        },
        scrollToBottom() {
            this.$nextTick(() => {
                const chatBox = this.$refs.chatBox;
                if (chatBox) {
                    chatBox.scrollTop = chatBox.scrollHeight;
                }
            });
        },
        async disconnectWebSocket() {
            try {
                 await axios.post(`${process.env.VUE_APP_API_BASE_URL}/chat/room/${this.roomId}/read`, {}, {
                    headers: { Authorization: `Bearer ${this.token}` }
                 });
            } catch(error) {
                console.error("Error marking messages as read:", error);
            }

            if (this.stompClient && this.stompClient.connected) {
                try {
                     const subscription = this.stompClient.subscriptions[`/topic/${this.roomId}`];
                     if (subscription) {
                        this.stompClient.unsubscribe(subscription.id);
                     } else {
                        this.stompClient.unsubscribe(`/topic/${this.roomId}`);
                     }

                     this.stompClient.disconnect(() => {
                         console.log("WebSocket disconnected.");
                         this.stompClient = null;
                     });
                } catch (error) {
                     console.error("Error during WebSocket disconnect:", error);
                     this.stompClient = null;
                }
            } else {
                this.stompClient = null;
            }
        },
        isSameDate(date1, date2) {
            if (!date1 || !date2) return false;
            const d1 = new Date(date1);
            const d2 = new Date(date2);
            return d1.getFullYear() === d2.getFullYear() &&
                   d1.getMonth() === d2.getMonth() &&
                   d1.getDate() === d2.getDate();
        },
        shouldShowDateSeparator(index) {
            if (index === 0) return true;
            const currentMsg = this.messages[index];
            const prevMsg = this.messages[index - 1];
            if (!currentMsg?.createdAt || !prevMsg?.createdAt) return false;
            return !this.isSameDate(currentMsg.createdAt, prevMsg.createdAt);
        },
        formatDate(timestamp) {
             if (!timestamp) return '';
             const date = new Date(timestamp);
             const year = date.getFullYear();
             const month = date.getMonth() + 1;
             const day = date.getDate();
             const days = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];
             const dayOfWeek = days[date.getDay()];
             return `${year}년 ${month}월 ${day}일 ${dayOfWeek}`;
        },
        formatTime(timestamp) {
            if (!timestamp) return '';
            const date = new Date(timestamp);
            let hours = date.getHours();
            const minutes = date.getMinutes().toString().padStart(2, '0');
            const ampm = hours >= 12 ? '오후' : '오전';
            hours = hours % 12;
            hours = hours ? hours : 12;
            const hoursStr = hours.toString().padStart(2, '0');
            return `${ampm} ${hoursStr}:${minutes}`;
        }
    },
}
</script>

<style>
.sent {
    text-align: right;
    margin-left: auto;
    margin-right: 0;
}

.received {
    text-align: left;
    margin-right: auto;
    margin-left: 0;
}

.chat-card {
    border-radius: 0;
    box-shadow: none;
    border: 1px solid #eaeaea;
    overflow: hidden;
}

.chat-box {
    height: 400px;
    overflow-y: auto;
    border: none;
    margin-bottom: 0;
    background-color: #ffffff;
    padding: 0;
}

.chat-message {
    margin-bottom: 0;
    position: relative;
    padding: 15px 20px;
    width: 100%;
    display: block;
    box-sizing: border-box;
}

.message-header {
    width: 100%;
    display: block;
    margin-bottom: 4px;
}

.message-content {
    display: block;
    max-width: 100%;
    word-wrap: break-word;
    color: #333;
    font-size: 14px;
    line-height: 1.4;
}

.message-time {
    font-size: 0.75rem;
    color: #888;
}

.chat-message strong {
    color: #555;
    font-weight: 500;
}

.input-area {
    border-top: 1px solid #f0f0f0;
    background-color: #fafafa;
    padding: 10px 15px;
}

.message-input {
    flex-grow: 1;
}

.retro-button {
    font-weight: 600;
    height: 40px;
    padding: 0 20px;
    line-height: 38px;
    outline: none;
    border: 1px solid black;
    background-color: #d3d3d3;
    border-radius: 0;
    position: relative;
    box-shadow: inset -1px -1px 0 0 #292929, inset 1px 1px 0 0 #ffffff, inset -2px -2px 0 0 #7f7f7f, inset 2px 2px 0 0 #ebebeb;
    font-family: 'Courier New', monospace;
    cursor: pointer;
    transition: transform 0.1s ease, box-shadow 0.1s ease;
    white-space: nowrap;
}

.retro-button:active {
    box-shadow: inset 1px 1px 0 0 #292929, inset -1px -1px 0 0 #ffffff, inset 2px 2px 0 0 #7f7f7f, inset -2px -2px 0 0 #ebebeb;
    transform: translateY(1px);
}

.d-flex {
    display: flex;
}

.align-center {
    align-items: center;
}

:deep(.v-field) {
    border-radius: 20px !important;
}

:deep(.v-field__outline) {
    color: #e0e0e0 !important;
}

:deep(.v-field__input) {
    padding: 8px 16px !important;
    min-height: 40px !important;
    height: 40px !important;
    line-height: normal;
    box-sizing: border-box;
}

.date-separator {
    display: block;
    width: fit-content;
    margin: 20px auto 15px auto;
    padding: 5px 12px;
    background-color: #e9ecf1;
    border-radius: 15px;
    color: #5f6368;
    font-size: 0.78rem;
    font-weight: 400;
    line-height: 1.4;
    text-align: left;
    border-bottom: none;
}

.date-separator .v-icon {
    color: #5f6368;
    margin-right: 4px;
    vertical-align: text-bottom;
}

.chat-message + .chat-message {
    border-top: 1px none;
}

.date-separator + .chat-message {
    border-top: none;
    margin-top: 0;
}

.chat-box > .date-separator:first-child + .chat-message {
    border-top: none;
}

.chat-message + .date-separator {
    margin-top: 20px;
}

</style>