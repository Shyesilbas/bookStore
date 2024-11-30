package com.serhat.bookstore.Controller;

import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/postComment")
    public ResponseEntity<PostCommentResponse> postComment(@RequestBody PostCommentRequest request , Principal principal){
        return ResponseEntity.ok(commentService.postComment(request, principal));
    }

    @GetMapping("/mostRatedCommentsForBook")
    public ResponseEntity<List<HighestRatedCommentsForBookResponse>> highestRatedCommentsForBook(@RequestParam String title , Principal principal){
        return ResponseEntity.ok(commentService.highestRatedCommentsForBook(title,principal));
    }

    @GetMapping("/leastRatedCommentsForBook")
    public ResponseEntity<List<leastRatedCommentsForBookResponse>> leastRatedCommentsForBook(@RequestParam String title , Principal principal){
        return ResponseEntity.ok(commentService.leastRatedCommentsForBook(title,principal));
    }



}
