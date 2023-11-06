package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @Test
    void PostCreate_create_Post() {
        // given
        PostCreate postCreate = PostCreate.builder()
                .content("HelloWorld")
                .writerId(1)
                .build();
        User writer = User.builder()
                .id(1L)
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .build();

        // when
        Post post = Post.from(writer, postCreate);

        // then
        assertThat(post.getContent()).isEqualTo("HelloWorld");
        assertThat(post.getWriter().getEmail()).isEqualTo("kok202@naver.com");
        assertThat(post.getWriter().getNickname()).isEqualTo("kok202");
        assertThat(post.getWriter().getAddress()).isEqualTo("Seoul");
        assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(post.getWriter().getCertificationCode()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    }

    @Test
    void PostUpdate_update_Post() {
    }
}