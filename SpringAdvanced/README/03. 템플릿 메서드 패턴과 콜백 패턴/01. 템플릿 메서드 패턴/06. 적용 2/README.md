# 템플릿 메서드 패턴 - 적용 2
- 템플릿 메서드 패턴 덕분에 변하는 코드와 변하지 않는 코드를 명확하게 분리했다.
- 로그를 출력하는 템플릿 역할을 하는 변하지 않는 코드는 모두 `AbstractTemplate`에 담아두고,
변하는 코드는 자식 클래스를 만들어서 분리했다.

## 지금까지 작성한 코드 비교
```java
//OrderServiceV0 코드
public void orderItem(String itemId) {
    orderRepository.save(itemId);
}
//OrderServiceV3 코드
public void orderItem(String itemId) {
    TraceStatus status = null;
    try {
        status = trace.begin("OrderService.orderItem()");
        orderRepository.save(itemId); //핵심 기능
        trace.end(status);
    } catch (Exception e) {
        trace.exception(status, e);
        throw e;
    }
}
//OrderServiceV4 코드
AbstractTemplate<Void> template = new AbstractTemplate<>(trace) {
    @Override
    protected Void call() {
        orderRepository.save(itemId);
        return null;
    }
};
template.execute("OrderService.orderItem()");
```
- `OrderServiceV0` : 핵심 기능만 있다.
- `OrderServiceV3` : 핵심 기능과 부가 기능이 함께 섞여 있다.
- `OrderServiceV4` : 핵심 기능과 템플릿을 호출하는 코드가 섞여 있다.
- V4는 템플릿 메서드 사용한 덕분에 핵심 기능에 좀 더 집중할 수 있게 되었다.

## 좋은 설계란?
- 좋은 설계라는 것은 무엇일까? 수 많은 정의가 있겠지만, 진정한 좋은 설계는 바로 변경이 일어날 때, 자연스럽게 드러난다.
- 지금까지 로그를 남기는 부분을 모아서 하나로 모듈화하고, 비즈니스 로직 부분을 분리했다.
- 여기서 만약 로그를 남기는 로직을 변경해야 된다면 `AbstractTemplate` 코드만 변경하면 된다.
- 템플릿이 없는 `V3` 상태에서 로그를 남기는 로직을 변경해야 한담녀 모든 클래스를 다 찾아서 고쳐야 한다. (수백개라면...)

## 단일 책임 원칙(SRP)
- `V4`는 단순히 템플릿 메서드 패턴을 적용해서 소스코드 몇줄을 줄인 것이 전부가 아니다.
- 로그를 남기는 부분에 단일 책임 원칙(SRP)을 지킨 것이다. 변경 지점을 하나로 모아서 변경에 쉽게 대처할 수 있는 구조를 만든 것이다.
