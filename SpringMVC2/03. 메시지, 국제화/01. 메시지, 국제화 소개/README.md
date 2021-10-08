# 메시지, 국제화 소개
## 메시지
- 여러 화면에 보이는 상품명, 가격, 수량 등, `label`에 있는 단어를 변경하려면
화면들을 다 찾아가면서 모두 변경해야 한다. 화면 수가 적으면 문제가 되지 않지만,
화면이 수십개 이상이라면 수십개의 파일을 모두 고쳐야 한다.
- 이런 다양한 메시지를 한 곳에서 관리하도록 하는 기능을 메시지 기능이라고 한다.
- 예를 들어서 `message.properties`라는 메시지 관리용 파일을 만들고
```properties
item=상품
item.id=상품 ID
item.itemName=상품명
item.price=가격
item.quantity=수량
```
- 각 html에서 다음과 같이 해당 데이터를 key 값으로 불러서 사용하는 것이다.
`<label for="itemName" th:text="#{item.itemName"></label>`
  
## 국제화
- 메시지에서 설명한 메시지 파일(`message.properties`)을 각 나라별로 별도로 관리하면 서비스를 국제화할 수 있다.
- 예를 들어서 다음 두개 파일 분류
    - messages_en.properties
    ```properties
    item=Item
    item.id=Item ID
    item.itemName=Item Name
    item.price=price
    item.quantity=quantity
    ```
    - messages_ko.properties
    ```properties
    item=상품
    item.id=상품 ID
    item.itemName=상품명
    item.price=가격
    item.quantity=수량
    ```
- 영어를 사용하는 사람이면 `message_en.properties`를 사용하고<br>
한국어를 사용하는 사람이면 `message_ko.properties`를 사용하게 개발하면 된다.
- 어디에서 접근한 것인지 인식하는 방법은 HTTP `accept-language`헤더 값을 사용하거나
사용자가 직 언어를 선택하도록 하고, 쿠키 등을 사용해서 처리하면 된다.

### 정리
- 메시지와 국제화 기능을 직접 구현할 수도 있겠지만, 스프링은 기본적인 메시지와 국제화 기능을 모두 제공한다.
- 그리고 타임리프도 스프링이 제공하는 메시자와 국제화 기능을 편리하게 통합해서 제공한다.
