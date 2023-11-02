package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)

})
class UserServiceTest {

    @Autowired
    private UserService userService;

    @DisplayName("")
    @Test
    void getByEmail_can_return_User_in_Active() {
      // given
        String email = "kok202@naver.com";

      // when
        UserEntity result = userService.getByEmail(email);

        // then
        assertThat(result.getNickname()).isEqualTo("kok202");
    }
    @DisplayName("")
    @Test
    void getByEmail_cannot_return_User_in_Pending() {
      // given
        String email = "kok303@naver.com";

      // when // then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("")
    @Test
    void getById_can_return_User_in_Active() {
      // given // when
        UserEntity result = userService.getById(1);

        // then
        assertThat(result.getNickname()).isEqualTo("kok202");
    }
    @DisplayName("")
    @Test
    void getById_cannot_return_User_in_Pending() {
      // given
        String email = "kok303@naver.com";

      // when // then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);
    }
}