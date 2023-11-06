package com.example.demo.post.controller.response;

import com.example.demo.post.domain.Post;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PostResponseTest {

    @Test
    void from() {
        // given
        Post post = Post.builder()
                .content("HelloWorld")
                .writer(User.builder()
                        .id(1L)
                        .email("kok202@naver.com")
                        .nickname("kok202")
                        .address("Seoul")
                        .status(UserStatus.ACTIVE)
                        .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                        .build())
                .build();

        // when
        PostResponse postResponse = PostResponse.from(post);

        // then
        assertThat(postResponse.getContent()).isEqualTo("HelloWorld");
        assertThat(postResponse.getWriter().getEmail()).isEqualTo("kok202@naver.com");
        assertThat(postResponse.getWriter().getNickname()).isEqualTo("kok202");
        assertThat(postResponse.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}