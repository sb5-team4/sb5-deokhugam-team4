package com.codeit.deokhugam.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberCreateRequest(
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    String email,
    @NotBlank(message = "닉네임을 입력해주세요")
    String nickname,
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
        message = "비밀번호는 최소 8자리 이상이며, 영문과 숫자를 포함해야 합니다"
    )
    String password
) {

}
