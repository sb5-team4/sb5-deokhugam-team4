package com.codeit.deokhugam.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
//Spring Boot가 자동으로 테스트용 DB(H2 등)로 교체하지 않도록 함.
//실제 Testcontainers PostgreSQL 컨테이너를 그대로 사용하려고 설정.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//컨테이너 기반 데이터베이스(PostgreSQL 등)를 테스트 환경에서 실행 가능하게 함.
public abstract class DataBaseConnectionSupport {

  protected static final PostgreSQLContainer<?> postgreSQLContainer;

  static {
    postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("db_name")
        .withUsername("root")
        .withPassword("password")
        .withNetworkAliases("db_container");

    postgreSQLContainer.start();
  }

  @DynamicPropertySource
  public static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
  }

}
