package com.codeit.deokhugam.dto.request.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberCreateRequest(
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Size(min = 3, max = 50)
    String email,
    @NotBlank(message = "닉네임을 입력해주세요")
    @Size(min = 1, max = 50)
    String nickname,
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
        message = "비밀번호는 최소 8자리 이상이며, 영문과 숫자를 포함해야 합니다"
    )
    @Size(min = 8, max = 50)
    String password
) {

}
