package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.demo.user.domain.UserStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        FakeMailSender fakeMailSender = new FakeMailSender();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        this.userService = UserServiceImpl.builder()
                .uuidHolder(new TestUuidHolder("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
                .clockHolder(new TestClockHolder(1678530673958L))
                .userRepository(fakeUserRepository)
                .certificationService(new CertificationService(fakeMailSender))
                .build();
        fakeUserRepository.save(
                User.builder()
                        .id(1L)
                        .email("kok202@naver.com")
                        .nickname("kok202")
                        .address("Seoul")
                        .status(UserStatus.ACTIVE)
                        .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                        .lastLoginAt(0L)
                        .build()
        );
        fakeUserRepository.save(
                User.builder()
                        .id(2L)
                        .email("kok303@naver.com")
                        .nickname("kok202")
                        .address("Seoul")
                        .status(UserStatus.PENDING)
                        .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                        .lastLoginAt(0L)
                        .build()
        );
    }

    @DisplayName("")
    @Test
    void getByEmail_can_return_User_in_Active() {
      // given
        String email = "kok202@naver.com";

      // when
        User result = userService.getByEmail(email);

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
            User result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("")
    @Test
    void getById_can_return_User_in_Active() {
      // given // when
        User result = userService.getById(1);

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
            User result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("")
    @Test
    void userCreateDto_create_user() {
      // given
        UserCreate userCreate = UserCreate.builder()
                .email("kok202@kakao.com")
                .address("Gyeongi")
                .nickname("kok202-k")
                .build();

        // when
        User result = userService.create(userCreate);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    }

    @DisplayName("")
    @Test
    void userUpdateDto_update_user() {
      // given
        UserUpdate userUpdate = UserUpdate.builder()
                .address("Incheon")
                .nickname("kok202-n")
                .build();

        // when
         userService.update(1, userUpdate);

        // then
        User user = userService.getById(1);
        assertThat(user.getId()).isNotNull();
        assertThat(user.getAddress()).isEqualTo("Incheon");
        assertThat(user.getNickname()).isEqualTo("kok202-n");
    }

    @DisplayName("")
    @Test
    void updateLastLogin_when_user_login() {
      // given
        // when
         userService.login(1);

        // then
        User userEntity = userService.getById(1);
        assertThat(userEntity.getLastLoginAt()).isEqualTo(1678530673958L);
    }

    @DisplayName("")
    @Test
    void active_user_pending() {
      // given
        // when
         userService.verifyEmail(2, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab");

        // then
        User userEntity = userService.getById(1);
        assertThat(userEntity.getStatus()).isEqualTo(ACTIVE);
    }

    @DisplayName("")
    @Test
    void error_user_pending_with_wrongVerifyCode() {
      // given // when  // then
        assertThatThrownBy(() ->
                userService.verifyEmail(2, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaac"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }

}