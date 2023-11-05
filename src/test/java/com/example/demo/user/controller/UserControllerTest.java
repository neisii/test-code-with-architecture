package com.example.demo.user.controller;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.demo.user.domain.UserStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/sql/user-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
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