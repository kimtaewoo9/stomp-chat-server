import { createApp } from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import router from '@/router/index.js'
import axios from 'axios';

const app = createApp(App);

// 요청 인터셉터 (기존 코드)
axios.interceptors.request.use(
    config => {
        const token = localStorage.getItem("token");
        if(token){
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 리다이렉트 진행 중인지 확인하는 플래그 변수
let isRedirecting = false;

// 응답 인터셉터
axios.interceptors.response.use(
    response => response, // 성공 응답은 그대로 반환
    error => {
        // 401 Unauthorized 에러 처리
        if (error.response && error.response.status === 401) {
            // 첫 번째 401 에러에서만 알림 표시 및 리다이렉트
            if (!isRedirecting) {
                isRedirecting = true;
                localStorage.removeItem('token');
                alert('로그인이 만료되었습니다. 다시 로그인해주세요.');
                router.push('/login');
                
                setTimeout(() => {
                    isRedirecting = false;
                }, 5000);
            }
            
            return new Promise(() => {});
        }
        
        // 401 이외의 에러는 계속 전파
        return Promise.reject(error);
    }
);

// axios를 전역 속성으로 등록
app.config.globalProperties.$axios = axios;

app.use(router);
app.use(vuetify);
app.mount('#app');