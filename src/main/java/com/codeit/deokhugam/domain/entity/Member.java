package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Setter
@ToString
@SuperBuilder

public class Member extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true)
  private String email;
  @Column(nullable = false, unique = true)
  private String nickname;
  @Column(nullable = false)
  private String password; // bcrypt
  @Column(nullable = false)
  private boolean deleted;

  public void encodePassword(String encodedPw) {
    this.password = encodedPw;
  }

  public void updateNickname(String nickname) {
    this.nickname = nickname;
  }
}
