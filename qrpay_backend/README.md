# 📅 작업 관리 보드 (26.03월 운영 반영 목표)

## 📌 Notice / Rule

### 공지

> 1. 신규 QRPAY 업무코드는 `CDP`
> 2. QRPAY는 PODMAN 컨테이너에서 운영 됨.
> 3. 세션사용 X

### 규칙

> 1. 지금은 Commit 히스토리를 관리할 필요가 없으니 일 단위로 작업한 부분 Commit
> 2. 주 1회 이상 회의

---

## 🔥 인프라/공통

- [x] 작업관리문서 작성 `2026.01.26`
- [ ] CDQ MCI 전문 EAI 이관
- [ ] 신규 EAI 엔드포인트 연동 신청
- [ ] PODMAN 컨테이너관련 빌드 및 프로세스 처리
- [ ] Web Url 관련 처리
- [ ] Refresh토큰 테이블 생성
- [ ] CDP 신규 공정 처리 수행
- [ ] Open API 연동 기관 호출 확인하기 (바디프렌즈, 코레인 , TJ)

---

## 💻 개발

### Backend⚙️ / Frontend📱

- [ ] (⚙️)BXI(EAI/MCI) 연동 공통 모듈 개발
- [ ] (⚙️)신규 EAI 3개 개발(CDQ MCI -> CPD EAI)
- [ ] (⚙️📱)Nice SMS인증
- [ ] (⚙️📱)보안키패드
- [ ] (⚙️📱)회원가입/ID, PW찾기 (EAI와 연동하여)
- [ ] (⚙️📱)Device 부분 처리 - setDevice (app과 연동하여)
- [ ] (⚙️📱)거래취소 - mpmqr의 EAI호출
- [ ] (⚙️📱)CPM결제 - mpmqr의 EAI호출 및 App 연동 url 확인
- [ ] (⚙️)DTO 클래스이름 형식 통일
- [ ] (⚙️)이력관련 테이블 로직
- [ ] (⚙️)REST API 로그 in/out DB Insert 관련

### 🏗️ 테스트코드

- [ ] Merchant 영역 테스트코드 작성
- [ ] Member 영역 테스트코드 작성
- [ ] Transaction 영역 테스트코드 작성
- [ ] Device 영역 테스트코드 작성
- [ ] LOG 영역 테스트코드 작성
- [ ] QRKIT 영역 테스트코드 작성
- [ ] Build시 H2 DB만 사용하여 테스트를 수행 부분 처리 개발

---

## 📝 문서작성

- [ ] API 문서 작성
- [ ] Build / 공통코드 문서
- [ ] Swagger 연동 및 RestAPI문서

---

## 📚 QRPAY Step2 - 2분기 수행

- [ ] DIPS EAI전문 이관
- [ ] WEBDB를 PGWDB로 이관
