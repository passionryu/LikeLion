package me.shinsunyoung.backend.Comment.Repostitory;

import me.shinsunyoung.backend.Comment.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long boardId);
}