package com.codeit.deokhugam.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberCreateRequest(
    @NotBlank
    @Email
    String email,
    @NotBlank
    String nickname,
    @NotBlank
    String password
) {

}
