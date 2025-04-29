# MarkMyCode
- **개발 기간** : 2025.01.24 ~ 2025.04.10
- **인원** : BackEnd 1명
- **서비스 링크** : [https://mark-my-code.com](https://mark-my-code.com) (테스트 계정: test01@code.com / Test1234!)
<br/>

## 프로젝트 개요
- **목표** : 다양한 플랫폼의 코딩 테스트 문제를 카테고리별로 정리하여 복습할 수 있는 블로그 서비스 제공
- **주요 기능**
  - **문제 풀이 등록 및 관리** : 사용자 풀이 등록·수정·삭제 기능 구현. 코드 에디터를 도입해 작성 편의성을 높임
  - **검색 및 필터링** : 문제 유형, 플랫폼, 언어별 필터링 기능으로 효율적 문제 검색 지원
  - **인증 시스템 구현** : Spring Security와 JWT 기반 인증, Google OAuth2 소셜 로그인 연동
  - **성능 개선** : JMeter를 활용한 API 응답 속도 테스트 및 최적화, 페이징 및 복합 인덱스를 통한 조회 성능 향상
  - **배포 및 클라우드 환경 설정** : Docker를 통한 컨테이너화 및 Oracle Cloud 기반 서비스 배포
  - **서버사이드 렌더링** : Thymeleaf 기반 동적 페이지 렌더링 및 AJAX 비동기 처리로 서버 부하 감소 및 UX 개선
<br/>

## 기술 스택
### BackEnd
<div style="display : flex">
    <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white"/>
    <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/> 
    <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
    <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white"/>
    <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
    <img src="https://img.shields.io/badge/JPA-007396?style=for-the-badge&logo=hibernate&logoColor=white"/>
    <img src="https://img.shields.io/badge/MyBatis-181717?style=for-the-badge&logo=myBatis&logoColor=white"/>
</div>
<br/>

### Tools
<div style="display : flex">
    <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/> 
    <img src="https://img.shields.io/badge/Oracle_Cloud-FF8C00?style=for-the-badge&logo=oracle&logoColor=white"/>
    <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white"/> 
    <img src="https://img.shields.io/badge/JMeter-FB2E00?style=for-the-badge&logo=jmeter&logoColor=white"/> 
</div>
<br/>

## Git Branch 전략
- `main` : 운영 및 배포용 브랜치
- `develop` : 로컬 개발 및 테스트용 브랜치, 기능 개발과 테스트 완료 후 `main` 브랜치로 직접 병합하여 배포
<br/>

## 테스트 및 검증
- **성능 테스트** : JMeter를 활용한 응답 속도 및 성능 테스트
- **API 검증** : Postman을 이용한 주요 API 정상 동작 검증
<br/>

## 설계 문서
- [ERD 보기](https://github.com/user-attachments/assets/3c89c5ac-b0dc-4473-8162-140dbc5c09c2)
<br/>

## 화면 설계
- [화면 설계 1](https://github.com/user-attachments/assets/22c22019-371e-4b14-b1f7-658a1ba1789c)
- [화면 설계 2](https://github.com/user-attachments/assets/9772cd82-caea-430b-8635-9147258e70db)
