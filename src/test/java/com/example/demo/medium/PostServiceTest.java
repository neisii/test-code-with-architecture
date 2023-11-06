package com.example.demo.medium;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/post-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    void getById_find_Content_That_exist() {
        Post result = postService.getById(1);

        assertThat(result.getWriter().getEmail()).isEqualTo("kok202@naver.com");
    }

    @Test
    void create() {
        PostCreate postCreate = PostCreate.builder()
                .content("aa")
                .writerId(1)
                .build();

        postService.create(postCreate);

        Post result = postService.getById(1);

        assertThat(result.getWriter().getId()).isEqualTo(1);

    }

    @Test
    void update() {
        PostUpdate postUpdate = PostUpdate.builder()
                .content("bbb")
                .build();

        postService.update(1, postUpdate);

        Post result = postService.getById(1);
        assertThat(result.getContent()).isEqualTo("bbb");
    }

    @SpringBootTest
    @AutoConfigureMockMvc
    @AutoConfigureTestDatabase
    @SqlGroup({
            @Sql(value = "/sql/post-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    static
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

    @SpringBootTest
    @AutoConfigureMockMvc
    @AutoConfigureTestDatabase
    @SqlGroup({
            @Sql(value = "/sql/post-create-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    static
    class PostCreateControllerTest {

        @Autowired
        private MockMvc mockMvc;
        private ObjectMapper objectMapper = new ObjectMapper();

        @Test
        void createPost() throws Exception {

            PostCreate postCreate = PostCreate.builder()
                    .content("aaaa")
                    .writerId(1)
                    .build();

            mockMvc.perform(post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postCreate)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.content").value("aaaa"))
                    .andExpect(jsonPath("$.writer.id").value(1))
                    .andExpect(jsonPath("$.writer.email").value("kok202@naver.com"))
                    .andExpect(jsonPath("$.writer.nickname").value("kok202"))
            ;
        }
    }
}