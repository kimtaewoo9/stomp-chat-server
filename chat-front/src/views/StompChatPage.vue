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
                                    @click="sendMessage"
                                >
                                    <v-icon class="mr-1">mdi-send</v-icon>
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
    border-radius: 8px !important;
    height: 40px;
    min-width: 80px !important;
    padding: 0 16px;
    font-size: 14px;
    font-weight: 500;
    text-transform: none;
    letter-spacing: normal;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    transition: all 0.2s ease;
}

.send-btn:hover {
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
    transform: translateY(-1px);
}

.send-btn .v-icon {
    font-size: 16px;
}

.d-flex {
    display: flex;
}

.align-center {
    align-items: center;
}

/* 텍스트 필드 스타일 개선 */
:deep(.v-field) {
    border-radius: 8px !important;
}

:deep(.v-field__outline) {
    color: #e0e0e0 !important;
}

:deep(.v-field__input) {
    padding: 8px 16px !important;
}

.v-btn.primary {
    background-color: #5181b8 !important;
}
</style>
