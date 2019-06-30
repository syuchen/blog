package com.developerchen.blog.module.comment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.developerchen.blog.module.comment.domain.dto.CommentDTO;
import com.developerchen.blog.module.comment.domain.entity.Comment;
import com.developerchen.core.service.IBaseService;

import java.util.List;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author syc
 */
public interface ICommentService extends IBaseService<Comment> {
    void replyComment(Comment comment);

    void deleteCommentById(long id);

    void deleteCommentByOwnerId(long ownerId);

    void updateStatusByCommentId(long id, String status);

    List<Comment> recentComments(long size);

    int commentCount(String status);

    Comment getCommentById(long id);

    IPage<CommentDTO> getCommentsByOwnerId(long postId, long page, long size);

    IPage<CommentDTO> getFlatCommentsByOwnerId(long ownerId, long page, long size);

    IPage<Comment> getCommentPage(long page, long size);
}