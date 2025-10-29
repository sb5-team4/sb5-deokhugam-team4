package com.codeit.deokhugam.config;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// CommentRepositoryTest에서 QuerydslConfig를 import한다.
@Configuration
public class QuerydslConfig {

  // JPA EntityManager 주입
  @PersistenceContext
  private EntityManager entityManager;

  // JPAQueryFactory를 Spring bean으로 등록
  @Bean
  public JPAQueryFactory queryFactory() {
    // 주입받은 entityManager를 사용해서 queryFactory생성한다.
    // 이것을 CommentRepositoryCustomImpl에서 사용한다.
    return new JPAQueryFactory(entityManager);
  }

}
