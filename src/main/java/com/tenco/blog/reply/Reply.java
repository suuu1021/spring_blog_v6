package com.tenco.blog.reply;

import com.tenco.blog.board.Board;
import com.tenco.blog.user.User;
import com.tenco.blog.utils.MyDateUtil;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name = "reply_tb")
@Entity
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // mysql, po, MS Server / 오라클은 12C 버전부터 지원(SEQUENCE)
    private Long id;

    @Column(nullable = false, length = 500) // 기본값 255
    private String comment;

    /**
     * 댓글 작성자
     * 한 명의 사용자가 여러 개의 댓글을 작성할 수 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 게시글에 댓글이 달릴 수 있다.
     * 하나의 게시글에는 여러 개의 댓글이 달릴 수 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @CreationTimestamp // 서버 pc 시간 기준
    private Timestamp createdAt;

    @Builder
    public Reply(Long id, String comment, User user, Board board, Timestamp createdAt) {
        this.id = id;
        this.comment = comment;
        this.user = user;
        this.board = board;
        this.createdAt = createdAt;
    }

    /**
     * Transient 데이터 베이스에 생성이 안되는 필드(즉 변수)
     * 왜 선언? - 뷰애 현재 로그인한 사용자가 여러 개의 댓글 중
     * 내가 작성한 댓글은 삭제 기능을 추가하기 위해 편의성 변수를 할당한다.
     */
    @Transient
    private boolean isReplyOwner;

    public boolean isOwner(Long sessionId) {
        return this.user.getId().equals(sessionId);
    }

    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }


}
