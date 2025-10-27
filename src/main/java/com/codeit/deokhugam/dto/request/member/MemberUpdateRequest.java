package com.codeit.deokhugam.dto.request.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(
    @NotBlank(message = "닉네임을 입력해주세요")
    @Size(min = 1, max = 50)
    String nickname
) {

}
