package com.example.demo.post.controller;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.example.demo.user.domain.UserStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostControllerTest {

    @DisplayName("사용자는 게시물을 단건 조회 할 수 있다")
    @Test
    void getPostById_return_post() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        Post post = Post.builder()
                .id(1L)
                .content("aaaa")
                .createdAt(1678530673958L)
                .writer(User.builder()
                        .id(1L)
                        .email("kok202@naver.com")
                        .nickname("kok202")
                        .address("Seoul")
                        .status(ACTIVE)
                        .lastLoginAt(100L)
                        .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                        .build())
                .build();
        testContainer.postRepository.save(post);

        // when// then
        ResponseEntity<PostResponse> result = testContainer.postController.getById(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).isEqualTo("aaaa");
        assertThat(result.getBody().getWriter().getNickname()).isEqualTo("kok202");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(1678530673958L);
    }

    @DisplayName("사용자가 존재하지 않는 게시물을  조회하면 에러가 발생한다")
    @Test
    void getPostById_return_404() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();

        // when// then
        assertThatThrownBy(() -> testContainer.postController.getById(1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @DisplayName("사용자는 게시물을 수정 할 수 있다")
    @Test
    void updatePost() {
        // given
        TestContainer testContainer = TestContainer.builder()
                .clockHolder(() -> 1678530674058L)
                .build();
        PostUpdate postUpdate = PostUpdate.builder()
                .content("blah")
                .build();
        Post post = Post.builder()
                .id(1L)
                .content("aaaa")
                .createdAt(1678530673958L)
                .writer(User.builder()
                        .id(1L)
                        .email("kok202@naver.com")
                        .nickname("kok202")
                        .address("Seoul")
                        .status(ACTIVE)
                        .lastLoginAt(100L)
                        .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                        .build())
                .build();
        testContainer.postRepository.save(post);
        testContainer.postService.update(1, postUpdate);

        // when// then
        ResponseEntity<PostResponse> result = testContainer.postController.getById(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).isEqualTo("blah");
        assertThat(result.getBody().getWriter().getNickname()).isEqualTo("kok202");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(1678530673958L);
        assertThat(result.getBody().getModifiedAt()).isEqualTo(1678530674058L);
    }

    @Test
    void toResponse() {
    }
}