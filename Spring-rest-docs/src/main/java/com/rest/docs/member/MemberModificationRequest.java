package com.rest.docs.member;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
public class MemberModificationRequest {
    @NotEmpty
    @Size(max = 10)
    private String name;
}
