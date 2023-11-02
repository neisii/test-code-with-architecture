package com.example.demo.repository;

import com.example.demo.model.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(showSql = true)
@Sql("/sql/user-repository-test-data.sql")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
//
//    @DisplayName("UserRepository가 제대로 연결되었다.")
//    @Test
//    void UserRepository_Connected() {
//      // given
//        UserEntity userEntity = new UserEntity();
//        userEntity.setEmail("aaa@naver.com");
//        userEntity.setAddress("Seoul");
//        userEntity.setNickname("aaaa");
//        userEntity.setStatus(UserStatus.ACTIVE);
//        userEntity.setCertificationCode("aaaa-aaa-aaaa-aaaaaaaaaaaa");
//
//        // when
//        UserEntity result = userRepository.save(userEntity);
//
//        // then
//        assertThat(result.getId()).isNotNull();
//
//    }

    @DisplayName("findByIdAndStatus는 사용자 데이터를 내려준다.")
    @Test
    void findByIdAndStatus_return_userData() {
        // given

        // when
        Optional<UserEntity> result = userRepository.findByIdAndStatus(1, UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();

    }

    @DisplayName("findByEmailAndStatus는 사용자 데이터를 내려준다.")
    @Test
    void findByEmailAndStatus_return_userData() {
        // given

        // when
        Optional<UserEntity> result = userRepository.findByEmailAndStatus("kok202@naver.com", UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();

    }

    @DisplayName("findByIdAndStatus는 데이터가 없으면 Optional_empty를 내려준다.")
    @Test
    void findByIdAndStatus_return_empty() {
        // given
        // when
        Optional<UserEntity> result = userRepository.findByIdAndStatus(2, UserStatus.ACTIVE);

        // then
        assertThat(result.isEmpty()).isTrue();
    }
}