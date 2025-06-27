package me.shinsunyoung.backend.Comment.Controller;


import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.Comment.DTO.CommentDTO;
import me.shinsunyoung.backend.Comment.Service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;


    @PostMapping
    public ResponseEntity<CommentDTO> save(@RequestBody CommentDTO commentDTO) {

        CommentDTO response = commentService.saveComment(commentDTO);
        return ResponseEntity.ok(response);
    }

    // 게시글의 전체 댓글+대댓글 계층 조회
    @GetMapping
    public List<CommentDTO> getAllComments(@RequestParam Long boardId) {
        return commentService.findCommentsByBoardId(boardId); // 반드시 계층구조 반환!
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id); // id로 댓글 삭제
        return ResponseEntity.ok().build();
    }

}