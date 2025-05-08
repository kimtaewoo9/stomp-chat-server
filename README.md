## 웹소켓/STOMP를 활용한 채팅서비스(spring, vue, redis)

#### 🔧 기술 스택
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=spring&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=JSON%20web%20tokens&logoColor=white)

![HTML](https://img.shields.io/badge/HTML5-E34F26?style=flat&logo=html5&logoColor=white)
![CSS](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=javascript&logoColor=black)
![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=flat&logo=vue.js&logoColor=white)
![Vuetify](https://img.shields.io/badge/Vuetify-1867C0?style=flat&logo=vuetify&logoColor=white)
![Vue Router](https://img.shields.io/badge/Vue_Router-41B883?style=flat&logo=vue.js&logoColor=white)
![SockJS](https://img.shields.io/badge/SockJS-000000?style=flat&logo=socket.io&logoColor=white)



#### 📝 개요
Spring Boot와 Vue.js를 활용한 실시간 채팅 서비스로,

웹소켓과 STOMP 프로토콜을 기반으로 1:1 채팅 및 그룹 채팅 기능을 제공합니다.

Redis의 pub/sub 기능을 활용하여 다중 서버 환경에서의 확장성을 고려한 설계를 적용했습니다.

🔗 **데모 사이트**: [stomp-chat-server.up.railway.app](https://stomp-chat-server.up.railway.app/)

<details>
  <summary>채팅방 화면</summary>
    <img width="700" alt="image" src="https://github.com/user-attachments/assets/b55d0fd5-78ab-4125-9f16-c4b0f231b729" />
</details>

<details>
  <summary>채팅방 목록 화면</summary>
    <img width="1243" alt="image" src="https://github.com/user-attachments/assets/8193b37e-37b9-41bc-8cdd-435f5c0f10ea" />
</details>

<details>
  <summary>회원가입 화면</summary>
    <img width="914" alt="image" src="https://github.com/user-attachments/assets/73b505ad-9b26-4409-a2b2-2efa52cb03da" />
</details>

<details>
  <summary>로그인 화면</summary>
    <img width="919" alt="image" src="https://github.com/user-attachments/assets/673134df-0314-4622-839b-0183bdb5ca66" />
</details>

## ✅ 개발 주요 API 목록
- stomp 통신 후 메시지 DB 저장
- 그룹채팅관련
  - 그룹채팅방 개설
  - 그룹채팅 목록조회
  - 그룹채팅에 참여자추가
- 1:1채팅관련
  - 개인채팅방 개설
- 공통사항
  - 이전 메시지 내역조회
  - 채팅방 메시지 읽음처리
  - 내 채팅방 목록조회
  - 채팅방 나가기

## redis의 pub/sub을 활용한 다중채팅서버 확장성 설계
