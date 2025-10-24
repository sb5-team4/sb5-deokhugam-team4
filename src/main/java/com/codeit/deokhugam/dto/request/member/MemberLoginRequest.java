package com.codeit.deokhugam.dto.request.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberLoginRequest(
    @NotBlank
    @Email
    String email,
    @NotBlank
    String password
) {

}
