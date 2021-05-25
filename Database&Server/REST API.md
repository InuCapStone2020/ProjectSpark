# REST API

이 문서는 REST API를 이용하여 재난문자의 검색 및 추가, 삭제, 변경 등의 기능을 구현하는 방법을 안내합니다. Parameter의 Required 여부는 URL 사용 기준입니다. Postman 등을 이용하여 JSON 객체를 요청하는 경우 생략이 가능한 Parameter가 있습니다.

---
## 재난문자 검색하기

DB에서 재난문자를 검색하는 기능입니다.

`GET` 방식으로 요청하고, 응답은 `JSON` 객체로 받습니다.

- **Request**
    
    URL
    ```http
    GET /search HTTP/1.1
    ```
    
    Parameter
    |Name|Type|Description|Required|
    |:---|:---|:---|:---|
    |region|String|검색 조건(지역)|O|
    |sdate|String|검색 조건(시작일)|O|
    |edate|String|검색 조건(종료일)|O|
    |event|String|검색 조건(사건 분류)|O|
    |page|Integer|검색 조건(페이지 구분)|O|
    
- **Response**

    Key
    |Name|Type|Description|
    |:---|:---|:---|
    |cnt|count[]|검색된 결과의 수를 가리키는 배열|
    |result|message[]|검색된 재난문자들의 배열|
    
    count
    |Name|Type|Description|
    |:---|:---|:---|
    |count|Integer|검색된 결과(재난문자)의 수(페이지 구분X)|
    
    message
    |Name|Type|Description|
    |:---|:---|:---|
    |NUM|Integer|재난문자의 고유번호|
    |SUBNUM|Integer|같은 NUM의 재난문자 구분을 위한 번호|
    |M_DATE|String|재난문자 발송일자|
    |M_TIME|String|재난문자 발송시간|
    |REGION|String|재난문자 발송지역|
    |CONTENT|String|재난문자 내용|
    |EVENT|String|재난문자의 사건분류|


---
## DB 내에 저장된 재난문자의 최초 날짜 구하기

DB에 저장된 재난문자의 최초 날짜를 구하는 기능입니다.

`GET` 방식으로 요청하고, 응답은 `JSON` 객체로 받습니다.

- **Request**
    
    URL
    ```http
    GET /mindate HTTP/1.1
    ```
    
    Parameter
    
    `No parameter`
    
- **Response**

    Key
    |Name|Type|Description|
    |:---|:---|:---|
    |mindate|String|DB에 저장된 재난문자의 최초 날짜|


---
## 재난문자 추가(삽입)하기

DB에 재난문자를 추가(삽입)하는 기능입니다.

`POST` 방식으로 요청합니다.

- **Request**

    URL
    ```http
    POST /addition HTTP/1.1
    ```

    Parameter
    |Name|Type|Description|Required|
    |:---|:---|:---|:---|
    |data|message[]|추가하려는 재난문자 리스트(배열)|O|
    
    message
    |Name|Type|Description|Required|
    |:---|:---|:---|:---|
    |NUM|Integer|재난문자의 고유번호|O|
    |SUBNUM|Integer|같은 NUM의 재난문자 구분을 위한 번호|O|
    |M_DATE|String|재난문자 발송일자|O|
    |M_TIME|String|재난문자 발송시간|O|
    |REGION|String|재난문자 발송지역|O|
    |CONTENT|String|재난문자 내용|O|
    |EVENT|String|재난문자의 사건분류|O|
    
- **Response**

    Key
    |Name|Type|Description|
    |:---|:---|:---|
    |message|String|성공 시 "Insert Successful"|
    
---
## 재난문자 수정(갱신)하기

DB에 저장된 재난문자의 NUM, SUBNUM 값을 이용하여 재난문자의 EVENT를 수정하는 기능입니다.

`PATCH` 방식으로 요청합니다.

- **Request**

    URL
    ```http
    PATCH /renewal HTTP/1.1
    ```

    Parameter
    |Name|Type|Description|Required|
    |:---|:---|:---|:---|
    |data|setting[]|수정하려는 재난문자의 설정 리스트(배열)|O|
    
    setting
    |Name|Type|Description|Required|
    |:---|:---|:---|:---|
    |NUM|Integer|수정하고자 하는 재난문자의 고유번호|O|
    |SUBNUM|Integer|수정하고자 하는 같은 NUM의 재난문자 구분을 위한 번호|O|
    |EVENT|String|수정 후 재난문자의 사건분류|O|
    
- **Response**

    Key
    |Name|Type|Description|
    |:---|:---|:---|
    |message|String|성공 시 "Update Successful"|

---
## 재난문자 삭제하기

DB에 저장된 재난문자의 NUM, SUBNUM 값을 이용하여 재난문자를 DB에서 제거하는 기능입니다.

`DELETE` 방식으로 요청합니다.

- **Request**

    URL
    ```http
    DELETE /elimination HTTP/1.1
    ```

    Parameter
    |Name|Type|Description|Required|
    |:---|:---|:---|:---|
    |data|delN[]|삭제하려는 재난문자의 설정 리스트(배열)|O|
    
    delN
    |Name|Type|Description|Required|
    |:---|:---|:---|:---|
    |NUM|Integer|삭제하고자 하는 재난문자의 고유번호|O|
    |SUBNUM|Integer|삭제하고자 하는 같은 NUM의 재난문자 구분을 위한 번호|O|
    
- **Response**

    Key
    |Name|Type|Description|
    |:---|:---|:---|
    |message|String|성공 시 "Eliminate Successful"|


