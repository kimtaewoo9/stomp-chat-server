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
                                class="chat-message"
                            >   
                                <div class="d-flex justify-space-between">
                                    <strong>{{ msg.senderName }}</strong>
                                    <div class="message-time">
                                        {{ formatTime(msg.createdAt) }}
                                    </div>
                                </div>
                                <div class="message-content">
                                    {{ msg.message }}
                                </div>
                            </div>
                        </div>
                        <div class="input-area pa-4">
                            <v-text-field
                                v-model="newMessage"
                                label="메시지 입력"
                                variant="outlined"
                                density="compact"
                                hide-details
                                @keyup.enter="sendMessage"
                            />
                            <v-btn color="primary" class="mt-2" block @click="sendMessage">전송</v-btn>
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
}

/* Vuetify 스타일 오버라이드 */
:deep(.v-text-field .v-field__outline__start),
:deep(.v-text-field .v-field__outline__end),
:deep(.v-text-field .v-field__outline__notch) {
    border-color: #e0e0e0 !important;
}

:deep(.v-text-field .v-field__input) {
    padding-top: 8px;
    padding-bottom: 8px;
}

.v-btn {
    text-transform: none;
    letter-spacing: normal;
    font-weight: normal;
}

.v-btn.primary {
    background-color: #5181b8 !important;
}
</style>
