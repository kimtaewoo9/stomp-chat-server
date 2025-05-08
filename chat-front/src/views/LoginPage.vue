<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" sm="4" md="6">
                <v-card class="mx-auto" max-width="450px" rounded="lg">
                    <v-card-title class="text-h5 text-center">로그인</v-card-title>
                    <v-card-text>
                        <div class="input-container">
                            <v-text-field 
                                label="email"
                                v-model="email"
                                type="email"
                                required
                                hide-details
                                variant="plain"
                                class="no-bottom-border"
                            ></v-text-field>

                            <v-divider></v-divider>
                            
                            <v-text-field 
                                label="password"
                                v-model="password"
                                type="password"
                                required
                                hide-details
                                variant="plain"
                            ></v-text-field>
                        </div>
                        <v-divider class="my-4"></v-divider>

                        <v-btn 
                            type="submit"
                            color="primary"
                            block class="mt-4 font-weight-bold"
                            @click="doLogin">로그인</v-btn>
                        
                        <!-- 회원가입 버튼 추가 -->
                        <v-btn 
                            block 
                            variant="outlined" 
                            color="primary" 
                            class="mt-2 font-weight-bold"
                            @click="goToSignup"
                        >
                            회원가입
                        </v-btn>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
export default{
    data(){
        return{
            email: "",
            password: "",
        }
    },
    methods:{
        async doLogin(){
            try{
                const loginData = {email:this.email, password:this.password};
                const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member/doLogin`, loginData);
                const token = response.data.token;
                const role = jwtDecode(token).role;
                const email = jwtDecode(token).sub;
                localStorage.setItem("token", token); // 토큰 받아서 localStorage에 저장함 .
                localStorage.setItem("role", role);
                localStorage.setItem("email", email);
                window.location.href="/";

                // 리다이렉트 정보가 있으면 해당 페이지로, 없으면 홈으로 이동
                const redirectPath = this.$route.query.redirect || '/';
                this.$router.push(redirectPath);
            } catch(error){
                console.error('로그인 오류:', error);
                let errorMessage = '이메일과 비밀번호를 확인해주세요.';

                alert(errorMessage);
                
                this.password = '';
            }
        },
        goToSignup() {
            // 회원가입 페이지로 이동
            this.$router.push('/member/create');
        }
    }
}
</script>
