package com.example.demo.medium;

import com.example.demo.post.domain.PostUpdate;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/sql/post-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getPostById_return_post() throws Exception {
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.content").value("helloworld"))
                .andExpect(jsonPath("$.writer.id").value(1))
                .andExpect(jsonPath("$.writer.nickname").value("kok202"))
        ;
    }

    @Test
    void getPostById_return_404() throws Exception {
        mockMvc.perform(get("/api/posts/55555"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Posts에서 ID 55555를 찾을 수 없습니다."))
        ;
    }

    @Test
    void updatePost() throws Exception {
        PostUpdate postUpdate = PostUpdate.builder()
                .content("blah")
                .build();

        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.content").value("blah"))
                .andExpect(jsonPath("$.writer.id").value(1))
                .andExpect(jsonPath("$.writer.nickname").value("kok202"))
        ;
    }

    @Test
    void toResponse() {
    }
}