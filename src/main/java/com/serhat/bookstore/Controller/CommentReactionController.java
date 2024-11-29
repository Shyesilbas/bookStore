package com.serhat.bookstore.Controller;

import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.service.CommentReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reaction")
public class CommentReactionController {
    private final CommentReactionService commentReactionService;

    @PostMapping("/likeComment")
    public ResponseEntity<LikeResponse> likeComment(@RequestBody LikeRequest request , Principal principal){
        return ResponseEntity.ok(commentReactionService.likeComment(request,principal));
    }
    @PostMapping("/dislikeComment")
    public ResponseEntity<DislikeResponse> dislikeComment(@RequestBody DislikeRequest request , Principal principal){
        return ResponseEntity.ok(commentReactionService.dislikeComment(request,principal));
    }
    @PostMapping("/repostComment")
    public ResponseEntity<RepostResponse> repostComment(@RequestBody RepostRequest request , Principal principal){
        return ResponseEntity.ok(commentReactionService.repostComment(request,principal));
    }


}
