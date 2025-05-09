<template>
    <div class="login-container">
      <h2 class="no-css-title">로그인</h2>
      <div class="no-css-card">
        <div class="no-css-form">
          <div class="no-css-field">
            <label for="email">이메일:</label>
            <input 
              id="email"
              type="email"
              v-model="email"
              class="no-css-input"
              required
            />
          </div>
          
          <div class="no-css-field">
            <label for="password">비밀번호:</label>
            <input 
              id="password"
              type="password"
              v-model="password"
              class="no-css-input"
              required
            />
          </div>
          
          <div class="no-css-actions">
            <button class="simple-btn primary" @click="doLogin">로그인</button>
            <button class="simple-btn" @click="goToSignup">회원가입</button>
          </div>
        </div>
      </div>
    </div>
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
  
  <style>
  .login-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    max-width: 450px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .no-css-card {
    border: 1px solid #000;
    padding: 20px;
    width: 100%;
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
  
  /* 심플한 버튼 스타일 */
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
  