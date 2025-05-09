<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" sm="4" md="6">
                <h2 class="no-css-title">회원가입</h2>
                <div class="no-css-card">
                    <div class="no-css-form">
                        <div class="no-css-field">
                            <label for="name">이름:</label>
                            <input 
                                id="name"
                                type="text"
                                v-model="name"
                                class="no-css-input"
                            />
                        </div>
                        
                        <div class="no-css-field">
                            <label for="email">이메일:</label>
                            <input 
                                id="email"
                                type="email"
                                v-model="email"
                                class="no-css-input"
                            />
                        </div>
                        
                        <div class="no-css-field">
                            <label for="password">비밀번호:</label>
                            <input 
                                id="password"
                                type="password"
                                v-model="password"
                                class="no-css-input"
                            />
                        </div>
                        
                        <div class="no-css-actions">
                            <button class="simple-btn primary" @click="memberCreate">가입하기</button>
                            <button class="simple-btn" @click="goToLogin">로그인 페이지로</button>
                        </div>
                    </div>
                </div>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import axios from 'axios';
export default{
    data(){
        return{
            name: "",
            email: "",
            password: "",
        }
    },
    methods:{
        async memberCreate(){
            const data = {
                name: this.name,
                email: this.email,
                password: this.password,
            }
            await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member/create`, data);
            this.$router.push("/login");
        },
        goToLogin() {
            // 로그인 페이지로 이동
            this.$router.push('/login');
        }
    }
}
</script>

<style>
.no-css-card {
    border: 1px solid #000;
    padding: 20px;
    max-width: 450px;
    margin: 0 auto;
    font-family: monospace;
}

.no-css-title {
    text-align: center;
    margin-bottom: 20px;
    font-weight: normal;
    font-family: monospace;
}

.no-css-form {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.no-css-field {
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.no-css-input {
    border: 1px solid #000;
    padding: 5px;
    font-family: monospace;
    background: none;
}

.no-css-actions {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-top: 15px;
}

/* 새로운 심플한 버튼 스타일 */
.simple-btn {
    font-family: monospace;
    background: none;
    border: 1px solid #000;
    padding: 8px 12px;
    cursor: pointer;
    transition: all 0.2s ease;
    letter-spacing: 1px;
    text-transform: uppercase;
    font-size: 0.9em;
}

.simple-btn:hover {
    background-color: #000;
    color: #fff;
}

.simple-btn.primary {
    background-color: #000;
    color: #fff;
}

.simple-btn.primary:hover {
    background-color: #333;
    border-color: #333;
}
</style>
