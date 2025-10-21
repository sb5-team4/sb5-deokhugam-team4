package com.codeit.deokhugam.domain.entity;

import com.codeit.deokhugam.domain.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;

@Entity
public class PopularBook extends BaseEntity {

  @Column(nullable = false)
  private String period;
  @Column(nullable = false)
  private Short rank;
  @Column(nullable = false)
  private BigDecimal score;


  @OneToOne(orphanRemoval = true)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

}
