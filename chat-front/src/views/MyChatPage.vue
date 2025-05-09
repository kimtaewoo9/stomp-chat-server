<template>
    <v-container>
        <v-row>
            <v-col>
                <v-card>
                    <v-card-title class="text-center text-h5">
                        내 채팅 목록
                    </v-card-title>
                    <v-card-text>
                        <v-table>
                            <thead>
                                <tr>
                                    <th>채팅방 이름</th>
                                    <th>읽지 않은 메시지</th>
                                    <th>액션</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="chat in chatList" :key="chat.roomId">
                                    <td>{{chat.roomName}}</td>
                                    <td>{{chat.unReadCount}}</td>
                                    <td>
                                        <button class="no-css-btn" @click="enterChatRoom(chat.roomId)">입장</button>
                                        <button 
                                            class="no-css-btn" 
                                            :disabled="chat.isGroupChat === 'N'" 
                                            @click="leaveChatRoom(chat.roomId)"
                                            :style="chat.isGroupChat === 'N' ? 'opacity: 0.5; cursor: not-allowed;' : ''"
                                        >
                                            나가기
                                        </button>
                                    </td>
                                </tr>
                            </tbody>
                        </v-table>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import axios from 'axios';
export default{
    data(){
        return {
            chatList: []
        }
    },
    async created(){
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/chat/my/rooms`);
        this.chatList = response.data;
    },
    methods: {
        enterChatRoom(roomId){
            this.$router.push(`/chatpage/${roomId}`);
        },
        async leaveChatRoom(roomId){
            await axios.delete(`${process.env.VUE_APP_API_BASE_URL}/chat/room/group/${roomId}/leave`);
            this.chatList = this.chatList.filter(chat => chat.roomId !== roomId);
        }
    },
}
</script>

<style>
.no-css-btn {
    font-family: monospace;
    background: none;
    border: 1px solid #000;
    padding: 2px 6px;
    margin: 2px;
    cursor: pointer;
}

.no-css-btn:hover:not([disabled]) {
    background-color: #f0f0f0;
}

.no-css-btn:disabled {
    border-color: #999;
    color: #999;
}
</style>
