<!-- HomePage.vue -->
<template>
    <div class="home-container">
      <div class="intro-section">
        <h1>STOMP CHAT</h1>
        <p class="tagline">A real-time communication platform</p>
        
        <div class="description">
          <p>A real-time chat service built with Spring Boot and Vue.js</p>
          <p>This platform utilizes WebSocket and STOMP protocol to provide seamless 1:1 and group chat functionality</p>
          <p>Designed with Redis pub/sub mechanism for scalability in multi-server environments</p>
        </div>
        
        <div class="action-buttons">
          <router-link to="/login" class="underline-btn">Login</router-link>
          <router-link to="/member/create" class="underline-btn">Sign Up</router-link>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'HomePage'
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
    font-size: 2.5rem;
    margin-bottom: 0.5rem;
    color: #333;
  }
  
  .tagline {
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
  
  @media (max-width: 600px) {
    .home-container {
      margin: 40px auto;
    }
    
    h1 {
      font-size: 2rem;
    }
    
    .tagline {
      font-size: 1.1rem;
    }
    
    .action-buttons {
      flex-direction: column;
      gap: 1.5rem;
    }
  }
  </style>
  