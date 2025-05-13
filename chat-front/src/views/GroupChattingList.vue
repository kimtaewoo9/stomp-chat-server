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
                                        <div class="room-name-container">
                                            {{chat.roomName}}
                                            <v-icon 
                                                v-if="chat.isPrivate || chat.private" 
                                                size="small" 
                                                color="grey" 
                                                class="ml-2"
                                            >
                                                mdi-lock
                                            </v-icon>
                                        </div>
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
        <v-dialog v-model="showCreateRoomModal" max-width="400px">
            <v-card class="modal-card">
                <v-card-title class="modal-title">
                    채팅방 생성
                    <v-icon class="close-icon" @click="showCreateRoomModal = false">mdi-close</v-icon>
                </v-card-title>
                <v-card-text class="modal-content">
                    <v-text-field 
                        label="방제목" 
                        v-model="newRoomTitle"
                        variant="outlined"
                        class="mb-4"
                        density="compact"
                    />
                    <v-checkbox
                        v-model="isPrivateRoom"
                        label="비밀방으로 설정"
                        color="grey-darken-2"
                        density="compact"
                    ></v-checkbox>
                    <v-text-field 
                        v-if="isPrivateRoom"
                        label="비밀번호" 
                        v-model="roomPassword"
                        variant="outlined"
                        density="compact"
                        :type="showPassword ? 'text' : 'password'"
                        :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                        @click:append="showPassword = !showPassword"
                    ></v-text-field>
                </v-card-text>
                <v-card-actions class="modal-actions">
                    <button class="no-css-btn cancel-btn" @click="showCreateRoomModal = false">
                        취소
                    </button>
                    <button class="no-css-btn action-btn" @click="createChatRoom">
                        생성
                    </button>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <!-- 비밀번호 입력 모달 -->
        <v-dialog v-model="showPasswordModal" max-width="350px">
            <v-card class="modal-card">
                <v-card-title class="modal-title">
                    비밀번호 입력
                    <v-icon class="close-icon" @click="showPasswordModal = false">mdi-close</v-icon>
                </v-card-title>
                <v-card-text class="modal-content">
                    <div class="password-header">
                        <v-icon color="grey-darken-1" size="large">mdi-lock</v-icon>
                        <p class="password-info">이 채팅방은 비밀번호로 보호되어 있습니다</p>
                    </div>
                    <v-text-field 
                        label="비밀번호" 
                        v-model="inputPassword"
                        variant="outlined"
                        density="compact"
                        :type="showInputPassword ? 'text' : 'password'"
                        :append-icon="showInputPassword ? 'mdi-eye' : 'mdi-eye-off'"
                        @click:append="showInputPassword = !showInputPassword"
                    ></v-text-field>
                </v-card-text>
                <v-card-actions class="modal-actions">
                    <button class="no-css-btn cancel-btn" @click="showPasswordModal = false">
                        취소
                    </button>
                    <button class="no-css-btn action-btn" @click="verifyAndJoin">
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
            if(chat.isPrivate || chat.private) {
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
            try {
                const roomData = {
                    roomName: this.newRoomTitle,
                    private: this.isPrivateRoom,
                    password: this.isPrivateRoom ? this.roomPassword : null
                };
                
                console.log('Room data being sent:', JSON.stringify(roomData));
                
                await axios.post(`${process.env.VUE_APP_API_BASE_URL}/chat/room/group/create`, roomData);
                
                this.showCreateRoomModal = false;
                this.newRoomTitle = "";
                this.isPrivateRoom = false;
                this.roomPassword = "";
                this.loadChatRoom();
            } catch (error) {
                console.error('Error creating chat room:', error);
                
                if (error.response) {
                    console.error('서버가 응답을 반환한 경우');
                    console.error('Error status:', error.response.status);
                    console.error('Error data:', error.response.data);
                    console.error('Error headers:', error.response.headers);
                } else if (error.request) {
                    console.error('요청이 전송되었지만 응답이 없는 경우');
                    console.error('No response received:', error.request);
                } else {
                    console.error('요청 설정 중 오류가 발생한 경우');
                    console.error('Request error:', error.message);
                }
            }
        },
        async loadChatRoom(){
            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/chat/room/group/list`);
            console.log('Chat room list:', response.data);
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
    padding: 6px 16px;
    margin: 2px;
    cursor: pointer;
    transition: all 0.2s ease;
    border-radius: 3px;
}

.no-css-btn:hover {
    background-color: #f0f0f0;
}

.modal-card {
    border: 1px solid #e0e0e0;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1) !important;
}

.modal-title {
    position: relative;
    border-bottom: 1px solid #f0f0f0;
    padding: 14px 16px;
    font-family: monospace;
    font-weight: 600;
    font-size: 1rem;
}

.close-icon {
    position: absolute;
    right: 12px;
    top: 12px;
    cursor: pointer;
    opacity: 0.6;
}

.close-icon:hover {
    opacity: 1;
}

.modal-content {
    padding: 16px;
}

.modal-actions {
    padding: 8px 16px 16px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.cancel-btn {
    border-color: #e0e0e0;
    color: #757575;
}

.cancel-btn:hover {
    background-color: #f5f5f5;
}

.action-btn {
    border-color: #424242;
    background-color: #616161;
    color: #000;
    font-weight: 500;
}

.action-btn:hover {
    background-color: #424242;
}

.password-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 16px;
}

.password-info {
    margin-top: 8px;
    color: #757575;
    font-family: monospace;
    text-align: center;
    font-size: 0.9rem;
}

.room-name-container {
    display: flex;
    align-items: center;
}
</style>
