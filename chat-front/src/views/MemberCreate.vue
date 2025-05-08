<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" sm="4" md="6">
                <v-card class="mx-auto" max-width="450px" rounded="lg">
                    <v-card-title class="text-h5 text-center">회원가입</v-card-title>
                    <v-card-text>
                        <div class="input-container">
                            <v-text-field 
                                label="name"
                                v-model="name"
                                type="name"
                                required
                                hide-details
                                variant="plain"
                                class="no-bottom-border"
                            ></v-text-field>

                            <v-divider></v-divider>
                            
                            <v-text-field 
                                label="email"
                                v-model="email"
                                type="email"
                                required
                                hide-details
                                variant="plain"
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

                        <v-btn type="submit" color="primary" block class="mt-4 font-weight-bold" @click="memberCreate">가입하기</v-btn>
                        
                        <!-- 로그인 페이지로 돌아가는 버튼 -->
                        <v-btn 
                            block 
                            variant="outlined" 
                            color="primary" 
                            class="mt-2 font-weight-bold"
                            @click="goToLogin"
                        >
                            로그인 페이지로
                        </v-btn>
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
