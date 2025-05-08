<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" md="8">
                <v-card>
                    <v-card-text>
                        <div class="chat-box">
                            <div 
                                v-for="(msg, index) in messages"
                                :key="index"
                                :class="['chat-message', msg.senderEmail === this.senderEmail ? 'sent' : 'received' ]"
                            >   
                                <div class="message-time" :class="{ 'time-sent': msg.senderEmail === this.senderEmail, 'time-received': msg.senderEmail !== this.senderEmail }">
                                    {{ formatTime(msg.createdAt) }}
                                </div>
                                <div class="message-content">
                                    <strong>{{ msg.senderName }}: </strong> {{ msg.message }}
                                </div>
                            </div>
                        </div>
                        <v-text-field
                            v-model="newMessage"
                            label="메시지 입력"
                            @keyup.enter="sendMessage"
                        />
                        <v-btn color="primary" block @click="sendMessage">전송</v-btn>
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
.chat-box{
    height: 300px;
    overflow-y: auto;
    border: 1px solid #ddd;
    margin-bottom: 10px;
}

.chat-message{
    margin-bottom: 10px;
    position: relative;
}

.message-content {
    display: inline-block;
    max-width: 80%;
    word-wrap: break-word;
}

.message-time {
    font-size: 0.75rem;
    color: #888;
    margin-top: 2px;
}

.sent{
    text-align: right;
}

.received{
    text-align: left;
}

.time-sent {
    text-align: right;
}

.time-received {
    text-align: left;
}

.v-card {
    border-radius: 12px;
    background-color: #f5f2e8;
    box-shadow: 5px 5px 0px #333;
    border: 3px solid #333;
}

/* 채팅박스 스타일 */
.chat-box {
    height: 300px;
    overflow-y: auto;
    border: 3px solid #333;
    margin-bottom: 15px;
    background-color: #fff8e7;
    font-family: 'Courier New', monospace;
    padding: 10px;
}

/* 채팅 메시지 스타일 */
.chat-message {
    margin-bottom: 15px;
    position: relative;
}

/* 메시지 내용 스타일 */
.message-content {
    display: inline-block;
    max-width: 80%;
    word-wrap: break-word;
    padding: 10px 15px;
    border-radius: 8px;
    font-size: 16px;
    line-height: 1.4;
}

/* 보낸 메시지 스타일 */
.sent .message-content {
    background-color: #ffcc66;
    border: 2px solid #333;
    color: #333;
    box-shadow: 3px 3px 0px #333;
}

/* 받은 메시지 스타일 */
.received .message-content {
    background-color: #66ccff;
    border: 2px solid #333;
    color: #333;
    box-shadow: 3px 3px 0px #333;
}

/* 시간 표시 스타일 */
.message-time {
    font-size: 0.75rem;
    color: #666;
    margin-top: 5px;
    font-family: 'Courier New', monospace;
    font-weight: bold;
}

/* 입력 필드 스타일 */
.v-text-field >>> .v-input__control {
    background-color: #fff8e7;
    border: 2px solid #333;
    border-radius: 8px;
    font-family: 'Courier New', monospace;
}

/* 버튼 스타일 */
.v-btn {
    background-color: #ff6b6b !important;
    color: white !important;
    font-weight: bold !important;
    border: 2px solid #333 !important;
    box-shadow: 3px 3px 0px #333 !important;
    border-radius: 8px !important;
    text-transform: uppercase !important;
    letter-spacing: 1px !important;
    transition: transform 0.2s !important;
}

.v-btn:hover {
    transform: translateY(2px) !important;
    box-shadow: 1px 1px 0px #333 !important;
}
</style>
