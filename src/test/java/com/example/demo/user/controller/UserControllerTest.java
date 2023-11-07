package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.port.AuthenticationService;
import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.demo.user.domain.UserStatus.ACTIVE;
import static com.example.demo.user.domain.UserStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class UserControllerTest {

    @DisplayName("사용자는 개인정보가 소거된 특정 유저의 정보를 받을 수 있다")
    @Test
    void getUserById_return_user() {
        // given
         TestContainer testContainer = TestContainer.builder()
                 .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build());

        // when// then
        ResponseEntity<UserResponse> result = testContainer.userController.getUserById(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getEmail()).isEqualTo("kok202@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("kok202");
        assertThat(result.getBody().getStatus()).isEqualTo(ACTIVE);
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100L);
    }

    @DisplayName("사용자는 존재하지 않는 유저의 아이디를 요청하는 경우 404응답을 받는다")
    @Test
    void getUserById_return_404() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        // when// then
        assertThatThrownBy(() -> {
            testContainer.userController.getUserById(1111);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("사용자는 인증 코드로 계정을 활성화 시킬 수 있다.")
    @Test
    void verifyEmail_active_userId() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build());

        // when// then
        ResponseEntity<Void> result = testContainer.userController.verifyEmail(1, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(testContainer.userRepository.getById(1).getStatus()).isEqualTo(ACTIVE);
    }

    @DisplayName("사용자는 개인정보가 소거된 특정 유저의 정보를 받을 수 있다")
    @Test
    void verifyEmail_forbidden() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build());

        // when// then
        ResponseEntity<Void> result = testContainer.userController.verifyEmail(2, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @DisplayName("사용자는 개인정보가 소거된 특정 유저의 정보를 받을 수 있다")
    @Test
    void getMyInfo_return_address() {
    }

    @DisplayName("사용자는 개인정보가 소거된 특정 유저의 정보를 받을 수 있다")
    @Test
    void updateMyInfo() {
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("kok202-n")
                .address("Pangyo")
                .build();

    }

    @Test
    void toResponse() {
    }

    @Test
    void toMyProfileResponse() {
    }
}