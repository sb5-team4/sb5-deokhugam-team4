package com.codeit.deokhugam.dto.request;

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
