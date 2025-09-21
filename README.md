## 웹소켓/STOMP를 활용한 채팅서비스(spring, vue, redis)

#### 개요
Spring Boot와 Vue.js를 활용한 실시간 채팅 서비스로,

웹소켓과 STOMP 프로토콜을 기반으로 1:1 채팅 및 그룹 채팅 기능을 제공합니다.

Redis의 pub/sub 기능을 활용하여 다중 서버 환경에서의 확장성을 고려한 설계를 적용했습니다.

🔗 **데모 사이트**: ~~[stomp-chat-server.up.railway.app](https://stomp-chat-server.up.railway.app/)~~ ⚠️


#### 홈페이지 화면
<img width="700" alt="image" src="https://github.com/user-attachments/assets/4186f731-2af9-4010-baa5-627c966f8f4b" />

#### 채팅방 화면
<img width="700" alt="image" src="https://github.com/user-attachments/assets/f6cfe899-0d43-4a49-a375-16ca61d6bdb8" />

## SKILLS

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![Railway](https://img.shields.io/badge/Railway-0B0D0E?style=flat&logo=railway&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=spring&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=JSON%20web%20tokens&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)

![HTML](https://img.shields.io/badge/HTML5-E34F26?style=flat&logo=html5&logoColor=white)
![CSS](https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=javascript&logoColor=black)
![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=flat&logo=vue.js&logoColor=white)
![SockJS](https://img.shields.io/badge/SockJS-000000?style=flat&logo=socket.io&logoColor=white)


## 개발 주요 API 목록
- 로그인 기능(JWT 방식)
- Oauth 기능 (네이버, 카카오, 구글로그인 기능)
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

## Redis의 pub/sub 기능을 활용한 다중채팅서버 확장성 설계
#### 멀티 서버에서 다른 서버에 있는 사용자에게 메시지를 전송할 수 없는 문제 발생
<img width="563" height="292" alt="image" src="https://github.com/user-attachments/assets/61920562-b11c-4b9a-89cd-23b99db76e7a" />

#### Redis Pub/Sub 기능을 활용한 아키텍처 설계
<img width="575" height="175" alt="image" src="https://github.com/user-attachments/assets/4aaf3d0a-0b27-4039-9e95-829c5a7d307f" />

<img width="575" height="196" alt="image" src="https://github.com/user-attachments/assets/cf3e1871-2796-4be9-9eb3-2cb16eea3966" />

<img width="575" height="216" alt="image" src="https://github.com/user-attachments/assets/baba25c3-d374-4a65-ab45-15463dae0b52" />


- kafka pub/sub과의 차이점
    - Redis는 더 빠른 성능
        - kafka는 디스크에 메시지를 저장하는데 반해, redis는 저장하지 않고 메모리 기반의 db로 더 빠른 성능 보장
    - kafka는 더 안정적인 메시징 처리
        - redis는 pub/sub과정에서 메시지를 저장하지 않기에, listen하는 서버가 없다면 메시지가 유실되는데 반해, kafka는 저장하여 추후에라도 전송 가능

## TODO
- 바이너리 파일 첨부
- 프로필 사진
- 안읽은 메시지 구분선
- ..

## 구현 완료 기능
- 채팅방 비밀번호 기능
- 토큰 만료 시 알림 후 로그인 화면으로 리다이렉트 
- 날짜 구분선
- ..
