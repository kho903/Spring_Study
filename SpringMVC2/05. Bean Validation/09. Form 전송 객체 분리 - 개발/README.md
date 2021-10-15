# Form 전송 객체 분리 - 개발
## Item
- Item 검증 코드 제거
## ItemSaveForm
- Item 저장용 폼
```java
@Data
public class ItemSaveForm {
  //...
}
```
  
## ItemUpdateForm
- Item 수정용 폼
```java
@Data
public class ItemUpdateForm{
    //...
}
```
## ValidationItemControllerV4
- 기존 코드 제거 : addItem(), addItemV2()
- 기존 코드 제거 : edit(), editV2()
- 추가 : addItem(), edit()

## 폼 객체 바인딩
```java
@PostMapping("/add")
public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
 //...
}
```
- Item 대신 ItemSaveform을 전달 받는다. 그리고 @Validated로 검증도 수행하고, BindingResult로 검증 결과도 받는다.

### 주의
- @ModelAttribute("item")에 item 이름을 넣어준 부분을 주의하자.
- 이것을 넣지 않으면 ItemSaveForm의 경우 규칙에 의해 itemSaveForm이라는 이름으로 MVC Model에 담기게 된다.
- 이렇게 되면 뷰 템플릿에서 접근하는 `th:object`이름도 함께 변경해주어야 한다.

## 폼 객체를 Item으로 변환
```java
//성공 로직
Item item = new Item();
item.setItemName(form.getItemName());
item.setPrice(form.getPrice());
item.setQuantity(form.getQuantity());
Item savedItem = itemRepository.save(item);
```
- 폼 객체의 데이터를 기반으로 Item 객체를 생성한다.
- 이렇게 폼 객체 처럼 중간에 다른 객체가 추가되면 변환하는 과정이 추가된다.

## 수정
```java
@PostMapping("/{itemId}/edit")
public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult) {
 //...
}
```
- 수정의 경우도 등록과 같다. 그리고 폼 객체를 Item 객체로 변환하는 과정을 거친다.

### 정리
- Form 전송 객체 분리해서 등록과 수정에 딱 맞는 기능을 구성하고, 검증도 명확히 분리했다.
