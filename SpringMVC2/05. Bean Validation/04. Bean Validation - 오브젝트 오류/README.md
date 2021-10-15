# Bean Validation - 오브젝트 오류
- Bean Validation 에서 특정 필드(FieldError)가 아닌 해당 오브젝트 관련 오류(ObjectError)는
`@ScriptAssert()`를 사용하여 처리한다.
```java
@Data
@ScripAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000")
public class Item {
    // ...    
}
```

## 메시지 코드
- `ScriptAssert.item`
- `ScriptAssert`
## 한계
- 실제로 사용해보면 제약이 많고 복잡하다.
- 실무에서는 검증 기능이 해당 객체의 범위를 넘어서는 경우들도 종종 등장하는데, 대응이 어렵다.
- 따라서 오브젝트 오류(글로벌 오류)의 경우 `@ScriptAssert`을 억지로 사용하는 것보다는 
오브젝트 오류 관련 부분만 직접 자바 코드로 작성하는 것을 권장한다.
```java
    // 특정 필드가 아닌 복합 툴 검증
    if (item.getPrice() != null && item.getQuantity() != null) {
        int resultPrice = item.getPrice() * item.getQuantity();
        if (resultPrice < 10000) {
            bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
        }
    }
```