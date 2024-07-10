# 1. 개발환경 및 실행환경
+ 개발환경
    1. IDE : Visual Studio Code
    2. OS : Mac OS (M2 chip)
    3. JDK : openjdk version "22.0.1" 2024-04-16

+ 실행환경
    1. 실행변수 : --spring.profiles.active=prod
# 2. 요구사항
+ 직원의 기본 연락 정보를 확인 할 수 있어야 한다
+ 직원 정보는 csv, Json 형태로 제공된다
## 파일 형식
### Json
```json
[
    {
        "name":"이무기",
        "email":"weapon@clivf.com",
        "tel":"010-1111-2424",
        "joined":"2020-01-05"
    },
    {
        "name":"판브이",
        "email":"panv@clivf.com",
        "tel":"010-3535-7979",
        "joined":"2013-07-01"
    },
    {
        "name":"차호빵",
        "email":"hobread@clivf.com",
        "tel":"010-8531-7942",
        "joined":"2019-12-05"
    }
]
```
### csv
```csv
김철수,charles@clovf.com,01075312468,2018.03.07
박영희,charles@clovf.com,01012345678,2021.04.28
홍길동,charles@clovf.com,01045677890,2018.03.07
```
## 구현 API
1. 전체조회 : http://localhost:8080/api/v1/members
2. 단일조회 : http://localhost:8080/api/v1/member/{이름}
3. 사용자 추가 : http://localhost:8080/api/v1/members

# 3. 요구사항 분석
## 1. 전체조회
단순하게 Member Entity를 전체조회 하는 JPA 함수를 리턴 한다.
## 2. 단일조회
이름으로 조회를 하기 때문에 동명이인이 발생 할 수 있다. 따라서 List 형태로 리턴한다.
## 3. 사용자 추가
+ 전체 조건
    + 사용자 추가는 [분기 조건]과 같이 분기되며, 1개의 API 에서 동작해야 한다.
    + csv , Json 형태의 입력에서 전화번호, 날짜형식은 서로 상이하다.
        + json : tel : 010-7531-2468, joined :2013-07-01
        + csv : tel : 01075312468, joined : 2018.03.07
    + 전화번호와 날짜는 다음과 같은 형식으로 API 내부에서 통일시켜 요구사항의 출력에 맞게 변경한다.
        + 전화번호 : 010-7531-2468
        + 날짜 : yyyy-MM-dd
    + 모든 응답은 HttpStatuc Code 기반으로 한다


+ 분기 조건
    + HTTP Body 가 Json 일 경우
    + HTTP Body 가 csv 일 경우
    + csv 형태의 파일이 첨부 될 경우
    + json 형태의 파일이 첨부 될 경우


# 4. 프로젝트 패키지 설명
## confg-aop
+ 각종 설정
    + controllerAdvice : ControllerAdvice 를 이용한 유효성 검증, 여기서는 전화번호를 정규식을 이용해 검증한다.
    + validation : 전화번호를 010-3535-7979 형태에 맞도록 수정한다.
## config-DummyLoader
 + 프로젝트 기동시 H2 DB에 더미 데이터를 삽입한다 (테스트 용도)
## controllers
 + REST API 컨트롤러
## entity
 + 사용자 정보를 저장하는 entity 객체 패키지
## exception
 + ExceptionHandler 를 이용한 커스텀 예외처리 설정
## utils
 + Csv 파일과 Json 관련된 유틸리티 Static 클래스