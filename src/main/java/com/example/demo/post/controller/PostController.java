package com.example.demo.post.controller;

import com.example.demo.post.controller.port.PostService;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.PostUpdate;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시물(posts)")
@Builder
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(PostResponse.from(postService.getById(id)));
    }

    private final PostService postService;

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable long id, @RequestBody PostUpdate postUpdate) {
        return ResponseEntity
            .ok()
            .body(PostResponse.from(postService.update(id, postUpdate)));
    }
}