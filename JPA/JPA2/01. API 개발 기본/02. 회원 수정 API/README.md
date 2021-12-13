# 회원 수정 API
```java
/**
 * 수정 API
 */
@PutMapping("/api/v2/members/{id}")
public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
@RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id,request.getName());
        Member findMember=memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(),findMember.getName());
        }

@Data
static class UpdateMemberRequest {
    private String name;
}

@Data
@AllArgsConstructor
static class UpdateMemberResponse {
    private Long id;
    private String name;
}
```
- 회원 수정도 DTO를 요청 파라미터로 매핑
```java
public class MemberService {
    private final MemberRepository memberRepository;

    /**
     * 회원 수정
     */
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
```
> 회원 수정 API `updateMemberV2`은 회원 정보를 부분 업데이트 한다. 여기서 PUT방식을 사용하는데
> 전체 업데이트 시 PUT 방식을 쓰기 떄문에, 부분 업데이트를 하려면 PATCH 또는 POST를 사용하는 것이 REST 스타일에 맞다.
