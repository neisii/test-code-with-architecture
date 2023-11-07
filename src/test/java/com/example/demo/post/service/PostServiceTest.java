package com.example.demo.post.service;

import com.example.demo.mock.FakePostRepository;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostServiceTest {

    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        FakePostRepository fakePostRepository = new FakePostRepository();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        this.postService = PostServiceImpl.builder()
                .postRepository(fakePostRepository)
                .userRepository(fakeUserRepository)
                .clockHolder(new TestClockHolder(1679530673958L))
                .build();
        User user1 = User.builder()
                .id(1L)
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
                .lastLoginAt(0L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("kok303@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .certificationCode("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaab")
                .lastLoginAt(0L)
                .build();
        fakeUserRepository.save(user1);
        fakeUserRepository.save(user2);
        fakePostRepository.save(
                Post.builder()
                        .id(1L)
                        .content("aa")
                        .writer(user1)
                        .createdAt(1678530673958L)
                        .modifiedAt(0L)
                        .build()
        );
    }

    @Test
    void getById_find_Content_That_exist() {
        Post result = postService.getById(1);

        assertThat(result.getContent()).isEqualTo("aa");
        assertThat(result.getWriter().getEmail()).isEqualTo("kok202@naver.com");
        assertThat(result.getCreatedAt()).isEqualTo(1678530673958L);
    }

    @Test
    void create() {
        PostCreate postCreate = PostCreate.builder()
                .content("bb")
                .writerId(1)
                .build();

        postService.create(postCreate);

        Post result = postService.getById(1);

        assertThat(result.getWriter().getId()).isEqualTo(1);

    }

    @Test
    void update() {
        PostUpdate postUpdate = PostUpdate.builder()
                .content("cc")
                .build();

        postService.update(1, postUpdate);

        Post result = postService.getById(1);
        assertThat(result.getContent()).isEqualTo("cc");
    }
}