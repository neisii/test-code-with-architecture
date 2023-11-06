package com.example.demo.medium;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.example.demo.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.example.demo.user.domain.UserStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DataJpaTest(showSql = true)
@Sql("/sql/user-repository-test-data.sql")
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;
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
//        UserEntity result = userJpaRepository.save(userEntity);
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
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(1, UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();

    }

    @DisplayName("findByEmailAndStatus는 사용자 데이터를 내려준다.")
    @Test
    void findByEmailAndStatus_return_userData() {
        // given

        // when
        Optional<UserEntity> result = userJpaRepository.findByEmailAndStatus("kok202@naver.com", UserStatus.ACTIVE);

        // then
        assertThat(result.isPresent()).isTrue();

    }

    @DisplayName("findByIdAndStatus는 데이터가 없으면 Optional_empty를 내려준다.")
    @Test
    void findByIdAndStatus_return_empty() {
        // given
        // when
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(2, UserStatus.ACTIVE);

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    @SpringBootTest
    @AutoConfigureMockMvc
    @AutoConfigureTestDatabase
    @SqlGroup({
            @Sql(value = "/sql/user-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    static
    class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserService userService;
        private ObjectMapper objectMapper = new ObjectMapper();


        @Test
        void getUserById_return_user() throws Exception {
            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("kok202@naver.com"))
                    .andExpect(jsonPath("$.nickname").value("kok202"))
                    .andExpect(jsonPath("$.address").doesNotExist())
                    .andExpect(jsonPath("$.status").value("ACTIVE"))
            ;
        }

        @Test
        void getUserById_return_404() throws Exception {
            mockMvc.perform(get("/api/users/55555"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Users에서 ID 55555를 찾을 수 없습니다."))
            ;
        }

        @Test
        void verifyEmail_active_userId() throws Exception {
            mockMvc.perform(get("/api/users/2/verify")
                            .queryParam("certificationCode", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab"))
                    .andExpect(status().isFound());

            User result = userService.getById(2);

            assertThat(result.getStatus()).isEqualTo(ACTIVE);
        }

        @Test
        void verifyEmail_forbidden() throws Exception {
            mockMvc.perform(get("/api/users/2/verify")
                            .queryParam("certificationCode", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaac"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getMyInfo_return_address() throws Exception {
            mockMvc.perform(get("/api/users/me")
                            .header("EMAIL", "kok202@naver.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.address").value("Seoul"))
                    .andExpect(jsonPath("$.nickname").value("kok202"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        void updateMyInfo() throws Exception {
            UserUpdate userUpdate = UserUpdate.builder()
                    .nickname("kok202-n")
                    .address("Pangyo")
                    .build();

            mockMvc.perform(put("/api/users/me")
                            .header("EMAIL", "kok202@naver.com")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userUpdate)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("kok202@naver.com"))
                    .andExpect(jsonPath("$.nickname").value("kok202-n"))
                    .andExpect(jsonPath("$.address").value("Pangyo"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        void toResponse() {
        }

        @Test
        void toMyProfileResponse() {
        }
    }

    @SpringBootTest
    @AutoConfigureMockMvc
    @AutoConfigureTestDatabase
    @SqlGroup({
            @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    static
    class UserCreateControllerTest {

        @Autowired
        private MockMvc mockMvc;
        @MockBean
        private JavaMailSender mailSender;
        private ObjectMapper objectMapper = new ObjectMapper();

        @Test
        void createUser_newUser_Status_is_PENDING()throws Exception {
            UserCreate userCreate = UserCreate.builder()
                    .email("kok202@kakao.com")
                    .nickname("kok202")
                    .address("Pangyo")
                    .build();
            BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            mockMvc.perform(post("/api/users")
                            .header("EMAIL", "kok202@naver.com")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userCreate)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.email").value("kok202@kakao.com"))
                    .andExpect(jsonPath("$.nickname").value("kok202"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
            ;
        }
    }
}