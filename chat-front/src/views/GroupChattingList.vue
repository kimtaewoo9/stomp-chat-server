<template>
    <v-container>
        <v-row>
            <v-col>
                <v-card>
                    <v-card-title class="text-center text-h5">
                        채팅방 목록
                        <div class="d-flex justify-end">
                            <button class="no-css-btn" @click="showCreateRoomModal = true">
                                채팅방 생성
                            </button>
                        </div>
                    </v-card-title>
                    <v-card-text>
                        <v-table>
                            <thead>
                                <tr>
                                    <th>방번호</th>
                                    <th>방제목</th>
                                    <th>채팅</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="chat in chatRoomList" :key="chat.roomId">
                                    <td>{{chat.roomId}}</td>
                                    <td>
                                        {{chat.roomName}}
                                        <v-icon v-if="chat.isPrivate" small>mdi-lock</v-icon>
                                    </td>
                                        
                                    <td>
                                        <button class="no-css-btn" @click="joinChatRoom(chat)">
                                            참여하기
                                        </button>
                                    </td>
                                </tr>
                            </tbody>
                        </v-table>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
        <v-dialog v-model="showCreateRoomModal" max-width="500px">
            <v-card>
                <v-card-title class="text-h6">
                    채팅방 생성
                </v-card-title>
                <v-card-text>
                    <v-text-field label="방제목" v-model="newRoomTitle"/>
                    <v-checkbox
                        v-model="isPrivateRoom"
                        label="비밀방으로 설정"
                    ></v-checkbox>
                    <v-text-field 
                        v-if="isPrivateRoom"
                        label="비밀번호" 
                        v-model="roomPassword"
                        :type="showPassword ? 'text' : 'password'"
                        :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                        @click:append="showPassword = !showPassword"
                    ></v-text-field>
                </v-card-text>
                <v-card-actions>
                    <button class="no-css-btn" @click="showCreateRoomModal = false">
                        취소
                    </button>
                    <button class="no-css-btn" @click="createChatRoom">
                        생성
                    </button>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <!-- 비밀번호 입력 모달 -->
        <v-dialog v-model="showPasswordModal" max-width="400px">
            <v-card>
                <v-card-title class="text-h6">
                    비밀번호 입력
                </v-card-title>
                <v-card-text>
                    <v-text-field 
                        label="비밀번호" 
                        v-model="inputPassword"
                        :type="showInputPassword ? 'text' : 'password'"
                        :append-icon="showInputPassword ? 'mdi-eye' : 'mdi-eye-off'"
                        @click:append="showInputPassword = !showInputPassword"
                    ></v-text-field>
                </v-card-text>
                <v-card-actions>
                    <button class="no-css-btn" @click="showPasswordModal = false">
                        취소
                    </button>
                    <button class="no-css-btn" @click="verifyAndJoin">
                        입장
                    </button>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script>
import axios from 'axios';
export default{
    data(){
        return {
            chatRoomList: [],
            showCreateRoomModal: false,
            newRoomTitle: "",
            isPrivateRoom: false,
            roomPassword: "",
            showPassword: false,
            showPasswordModal: false,
            inputPassword: "",
            showInputPassword: false,
            selectedRoom: null,
        }
    },
    async created(){
        this.loadChatRoom();
    },
    methods: {
        async joinChatRoom(chat){
            if(chat.isPrivate) {
                this.selectedRoom = chat;
                this.showPasswordModal = true;
            } else {
                await this.joinRoom(chat.roomId);
            }
        },
        async verifyAndJoin(){
            try {
                await axios.post(
                    `${process.env.VUE_APP_API_BASE_URL}/chat/room/group/${this.selectedRoom.roomId}/verify-password`, 
                    { password: this.inputPassword }
                );
                await this.joinRoom(this.selectedRoom.roomId);
                this.showPasswordModal = false;
                this.inputPassword = "";
            } catch(error) {
                alert("비밀번호가 일치하지 않습니다.");
            }
        },
        async joinRoom(roomId){
            await axios.post(`${process.env.VUE_APP_API_BASE_URL}/chat/room/group/${roomId}/join`);
            this.$router.push(`/chatpage/${roomId}`);
        },
        async createChatRoom(){
            const roomData = {
                roomName: this.newRoomTitle,
                isPrivate: this.isPrivateRoom,
                password: this.isPrivateRoom ? this.roomPassword : null
            };
            await axios.post(`${process.env.VUE_APP_API_BASE_URL}/chat/room/group/create`, roomData);
            this.showCreateRoomModal = false;
            this.newRoomTitle = "";
            this.isPrivateRoom = false;
            this.roomPassword = "";
            this.loadChatRoom();
        },
        async loadChatRoom(){
            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/chat/room/group/list`);
            this.chatRoomList = response.data;
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

.no-css-btn:hover {
    background-color: #f0f0f0;
}
</style>
