## 웹소켓/STOMP를 활용한 채팅서비스(spring, vue, redis)

1. websocket/stomp 기술을 활용한1:1채팅, 단체 채팅 등 시나리오별 채팅서비스 구현

- 순수 웹소켓을 활용한 채팅 구현
- stomp를 활용한 1:1채팅
- stomp를 활용한 그룹채팅

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

## 개발 주요 API 목록
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

## 기술스택
백엔드

java17, springboot3.4, spring data jpa, jwt, postgres, redis

프론트엔드

html/css/js, vue3, vuetify, vue-router, sockjs-client
