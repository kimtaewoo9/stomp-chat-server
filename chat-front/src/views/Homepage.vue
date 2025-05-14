<!-- HomePage.vue -->
<template>
  <div class="home-container">
    <div class="intro-section">
      <h1>
        STOMP CHAT <sup class="version">V1</sup>
      </h1>
      <p class="tagline">A real-time communication platform</p>
      
      <div class="description">
        <p>A real-time chat service built with Spring Boot and Vue.js</p>
        <p>This platform utilizes WebSocket and STOMP protocol to provide seamless 1:1 and group chat functionality</p>
        <p>Designed with Redis pub/sub mechanism for scalability in multi-server environments</p>
      </div>
      
      <div class="action-buttons" v-if="!isLoggedIn">
        <router-link to="/login" class="underline-btn">Login</router-link>
        <router-link to="/member/create" class="underline-btn">Sign Up</router-link>
      </div>
      <div class="action-buttons" v-else>
        <router-link to="/groupchatting/list" class="underline-btn">Go to Chat</router-link>
      </div>
      
      <div class="social-footer">
        <p>By 
          <a href="https://github.com/kimtaewoo9" target="_blank" class="social-link">
            <img src="/icons/github.svg" alt="GitHub" class="social-icon" /> 
            <span class="underline-text">kimtaewoo9</span>
          </a> | 
          <a href="https://instagram.com/ootaew11" target="_blank" class="social-link">
            <img src="/icons/instagram.svg" alt="Instagram" class="social-icon"  />
            <span class="underline-text">ootaew11</span>
          </a>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage',
  data() {
    return {
      isLoggedIn: false
    }
  },
  created() {
    this.checkLoginStatus();
    // localStorage 변경 감지를 위한 이벤트 리스너 추가
    window.addEventListener('storage', this.checkLoginStatus);
  },
  beforeUnmount() {
    // 컴포넌트 제거 시 이벤트 리스너 제거
    window.removeEventListener('storage', this.checkLoginStatus);
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem('token');
      this.isLoggedIn = token !== null && token !== undefined && token.length > 0;
    }
  }
}
</script>

<style scoped>
.home-container {
  max-width: 800px;
  margin: 80px auto;
  padding: 0 20px;
  text-align: center;
}

h1 {
  font-size: 3rem;
  margin-bottom: 0.5rem;
  font-weight: bold;
  background: linear-gradient(to right, #8e76d5, #e0699e);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  display: inline-block;
}

h1 .version {
  font-size: 0.3em;
  font-weight: bold;
  vertical-align: super;
  color: #333;

  margin-left: -3px;
}

.tagline {
  font-weight: bold;
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 2.5rem;
}

.description {
  margin-bottom: 3rem;
  line-height: 1.8;
}

.description p {
  margin-bottom: 1rem;
  color: #555;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 2rem;
}

.underline-btn {
  position: relative;
  font-size: 1.1rem;
  color: #333;
  text-decoration: none;
  padding-bottom: 5px;
  font-weight: 500;
}

.underline-btn::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 2px;
  bottom: 0;
  left: 0;
  background-color: #333;
  transform-origin: bottom right;
  transition: transform 0.3s ease-out;
}

.underline-btn:hover::after {
  transform: scaleX(1.1);
  transform-origin: bottom left;
}

/* 소셜 미디어 링크 스타일 */
.social-footer {
  margin-top: 3rem;
  font-size: 0.9rem;
  color: #777;
}

.social-link {
  color: #555;
  text-decoration: none;
  transition: color 0.3s;
  display: inline-flex;
  align-items: center;
}

.social-link:hover {
  color: #000;
}

/* social-icon 클래스가 h1 안에서도 사용되고, social-footer 안에서도 사용되므로,
   h1 내부의 아이콘 크기만 특별히 조정하려면 더 구체적인 선택자를 사용했습니다.
   기존 .social-icon 스타일은 유지됩니다. */
.social-footer .social-icon { /* 기존 스타일 유지 */
  width: 16px;
  height: 16px;
  margin-right: 4px;
}


.underline-text {
    position: relative;
    text-decoration-line: underline;
    text-decoration-color: currentColor; /* 텍스트와 같은 색상 사용 */
}

@media (max-width: 600px) {
  .home-container {
    margin: 40px auto;
  }
  
  h1 {
    font-size: 2.2rem; /* 모바일에서도 h1 폰트 크기 조정 */
  }

  h1 .social-icon {
    width: 1.8rem; /* 모바일 아이콘 크기 조정 */
    height: 1.8rem;
  }
  
  .tagline {
    font-size: 1.1rem;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .social-footer {
    margin-top: 2rem;
  }
}
</style>
