package com.example.demo.service;

import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.infrastructure.PostEntity;
import com.example.demo.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;

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
        PostEntity result = postService.getById(1);

        assertThat(result.getWriter().getEmail()).isEqualTo("kok202@naver.com");
    }

    @Test
    void create() {
        PostCreate postCreate = PostCreate.builder()
                .content("aa")
                .writerId(1)
                .build();

        postService.create(postCreate);

        PostEntity result = postService.getById(1);

        assertThat(result.getWriter().getId()).isEqualTo(1);

    }

    @Test
    void update() {
        PostUpdate postUpdate = PostUpdate.builder()
                .content("bbb")
                .build();

        postService.update(1, postUpdate);

        PostEntity result = postService.getById(1);
        assertThat(result.getContent()).isEqualTo("bbb");
    }
}