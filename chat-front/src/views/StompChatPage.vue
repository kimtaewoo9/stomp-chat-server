<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" md="8">
                <v-card flat class="chat-card">
                    <v-card-text class="pa-0">
                        <div class="chat-box">
                            <div 
                                v-for="(msg, index) in messages"
                                :key="index"
                                :class="['chat-message', msg.senderEmail === this.senderEmail ? 'sent' : 'received' ]"
                            >   
                                <div class="message-header" :class="{ 'text-right': msg.senderEmail === this.senderEmail, 'text-left': msg.senderEmail !== this.senderEmail }">
                                    <span class="message-time ml-2">{{ formatTime(msg.createdAt) }}</span>
                                </div>
                                <div class="message-content" :class="{ 'text-right': msg.senderEmail === this.senderEmail, 'text-left': msg.senderEmail !== this.senderEmail }">
                                    <strong>{{ msg.senderName }}: </strong>{{ msg.message }}
                                </div>
                            </div>
                        </div>
                        <div class="input-area pa-2">
                            <div class="d-flex align-center">
                                <v-text-field
                                    v-model="newMessage"
                                    placeholder="메시지 입력"
                                    variant="outlined"
                                    density="compact"
                                    hide-details
                                    class="message-input"
                                    @keyup.enter="sendMessage"
                                />
                                <v-btn 
                                    color="primary" 
                                    class="send-btn ml-2"
                                    elevation="2"
                                    @click="sendMessage"
                                >
                                    <v-icon>mdi-send</v-icon>
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

export default{
    data(){
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
    async created(){
        this.senderEmail = localStorage.getItem("email");
        this.roomId = this.$route.params.roomId;
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/chat/history/${this.roomId}`);
        this.messages = response.data;
        this.connectWebsocket();
    },
    // 사용자가 현재 라우트에서 다른 라우트로 이동하려고 할때 호출되는 훅함수
    beforeRouteLeave(to, from, next) {
        this.disconnectWebSocket();
        next();
    },
    // 화면을 완전히 꺼버렸을때
    beforeUnmount() {
        this.disconnectWebSocket();
    },
    methods: {
        connectWebsocket(){
            if(this.stompClient && this.stompClient.connected) return;
            // sockjs는 websocket을 내장한 향상된 js 라이브러리. http엔드포인트 사용.
            const sockJs = new SockJS(`${process.env.VUE_APP_API_BASE_URL}/connect`)
            this.stompClient = Stomp.over(sockJs);
            this.token = localStorage.getItem("token");
            this.stompClient.connect({
                Authorization: `Bearer ${this.token}`
            },
                ()=>{
                    this.stompClient.subscribe(`/topic/${this.roomId}`, (message) => {
                        const parseMessage = JSON.parse(message.body);
                        this.messages.push(parseMessage);
                        this.scrollToBottom();
                    },{Authorization: `Bearer ${this.token}`})
                }
            )
        },
        sendMessage(){
            if(this.newMessage.trim() === "") return;
            const message = {
                senderEmail: this.senderEmail,
                senderName: this.senderName,
                message: this.newMessage
            }
            this.stompClient.send(`/publish/${this.roomId}`, JSON.stringify(message));
            this.newMessage = ""
        },
        scrollToBottom(){
            this.$nextTick(()=>{
                const chatBox = this.$el.querySelector(".chat-box");
                chatBox.scrollTop = chatBox.scrollHeight;
            })
        },
        async disconnectWebSocket(){
            await axios.post(`${process.env.VUE_APP_API_BASE_URL}/chat/room/${this.roomId}/read`);
            if(this.stompClient && this.stompClient.connected){
                this.stompClient.unsubscribe(`/topic/${this.roomId}`);
                this.stompClient.disconnect();
            }
        },
        formatTime(timestamp) {
            if (!timestamp){
                return '';
            }
            
            const date = new Date(timestamp);
            const hours = date.getHours().toString().padStart(2, '0');
            const minutes = date.getMinutes().toString().padStart(2, '0');
            
            return `${hours}:${minutes}`;
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
    border-bottom: 1px solid #f0f0f0;
    width: 100%;
    display: block;
}

.message-header {
    width: 100%;
    display: block;
}

.message-content {
    display: block;
    max-width: 100%;
    word-wrap: break-word;
    margin-top: 5px;
    color: #333;
    font-size: 14px;
}

.message-time {
    font-size: 0.75rem;
    color: #888;
    margin-top: 2px;
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

.send-btn {
    min-width: 44px !important;
    width: 44px;
    height: 44px;
    border-radius: 50%;
    transition: all 0.3s ease;
    box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
    position: relative;
    overflow: hidden;
}

.send-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 8px rgba(0, 0, 0, 0.2);
}

.send-btn:active {
    transform: translateY(1px);
    box-shadow: 0 2px 3px rgba(0, 0, 0, 0.2);
}

.send-btn .v-icon {
    font-size: 20px;
    transform: rotate(-30deg) translateX(-2px);
}

.d-flex {
    display: flex;
}

.align-center {
    align-items: center;
}

/* 텍스트 필드 스타일 개선 */
:deep(.v-field) {
    border-radius: 20px !important;
}

:deep(.v-field__outline) {
    color: #e0e0e0 !important;
}

:deep(.v-field__input) {
    padding: 8px 16px !important;
}

.v-btn {
    text-transform: none;
    letter-spacing: normal;
    font-weight: normal;
}

.v-btn.primary {
    background-color: #5181b8 !important;
}

/* 버튼에 물결 효과 추가 */
.send-btn::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 5px;
    height: 5px;
    background: rgba(255, 255, 255, 0.5);
    opacity: 0;
    border-radius: 100%;
    transform: scale(1, 1) translate(-50%);
    transform-origin: 50% 50%;
}

.send-btn:focus:not(:active)::after {
    animation: ripple 1s ease-out;
}

@keyframes ripple {
    0% {
        transform: scale(0, 0);
        opacity: 0.5;
    }
    20% {
        transform: scale(25, 25);
        opacity: 0.3;
    }
    100% {
        opacity: 0;
        transform: scale(40, 40);
    }
}
</style>
