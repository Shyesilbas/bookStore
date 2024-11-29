package com.serhat.bookstore.Controller;

import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/postComment")
    public ResponseEntity<PostCommentResponse> postComment(@RequestBody PostCommentRequest request , Principal principal){
        return ResponseEntity.ok(commentService.postComment(request, principal));
    }

    @PostMapping("/likeComment")
    public ResponseEntity<LikeResponse> likeComment(@RequestBody LikeRequest request , Principal principal){
        return ResponseEntity.ok(commentService.likeComment(request,principal));
    }
    @PostMapping("/dislikeComment")
    public ResponseEntity<DislikeResponse> dislikeComment(@RequestBody DislikeRequest request , Principal principal){
        return ResponseEntity.ok(commentService.dislikeComment(request,principal));
    }
    @PostMapping("/repostComment")
    public ResponseEntity<RepostResponse> repostComment(@RequestBody RepostRequest request , Principal principal){
        return ResponseEntity.ok(commentService.repostComment(request,principal));
    }



}
