package com.tenco.blog.board;

import com.tenco.blog.reply.Reply;
import com.tenco.blog.user.User;
import com.tenco.blog.utils.MyDateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
// 기본 생성자 = JPA에서 Entity는 기본 생성자가 필요
@NoArgsConstructor
@Data
// @Table : 실제 데이터베이스 테이블 명을 지정할 때 사용
@Table(name = "board_tb")
// @Entity JPA가 이 클래스를 데이터베이스 테이블과 맵핑하는 객체(entity)로 인식
// 즉, @Entity 어노테이션이 있어야 JPA가 이 객체를 관리한다.
@Entity

public class Board {

    // @Id 이 필드가 기본키(Primary key)임을 나타냄
    @Id
    // IDENTITY 전략 : 데이터베이스의 기본 전략을 사용한다. -> Auto_Increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 별도 어노테이션이 없으면 필드명이 컬럼명이 됨
    private String title;
    private String content;

    // V2에서 제거: private String username;
    // V3에서 추가: Board 엔티티 User 엔티티와의 연관관계 성립

    // @ManyToOne 다대일 연관관계: 여러 게시글이 하나의 사용자에게 속함
    // FetchType.LAZY: 지연로딩으로 성능 최적화 (User 정보가 필요할 때만 조회)
    // @ManyToOne(fetch = FetchType.EAGER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키 컬럼명 명시
    private User user;

    // @CreationTimestamp : hibernate가 제공하는 어노테이션
    // Entity가 처음 저장할 때 현재 시간을 자동으로 설정한다.
    // pc -> db(날짜 주입)
    // v1 에서는 SQL now()를 직접 사용했지만 v2에서는 JPA가 자동 처리
    @CreationTimestamp
    private Timestamp createdAt; // created_at (스네이크 케이스로 자동 변환)

    // 테이블에 필드 만들지 않는 어노테이션
    // (현재 로그인한 유저와 게시글 작성자 여부를 판단 하기 위해 선언)
    @Transient
    private boolean isBoardOwner;


    // 게시글 소유자를 직접 확인하는 기능을 만들자
    public boolean isOwner(Long checkUserId) {
        return this.user.getId().equals(checkUserId);
    }

    // 머스태치에서 표현할 시간의 포맷기능(행위)을 스스로 만들자.
    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }

    /**
     * 게시글과 댓글을 양방향 맵핑으로 설계 하겠다.
     * 하나의 게시글(one)은 여러 개의 댓글(many)을 가질 수 있다.
     *
     * 테이블 기준으로 고민을 해본다면 게시글 테이블과 댓글 테이블 관계를
     * 형성할 때 FK는 누가 들고 있어야 맞는가?
     * 연관관계의 주인은 Reply 이 되어야 한다 (FK)
     * mappedBy : 외래키 주인이 아닌 엔티티에 설정해야 한다.
     *
     * cascade = CascadeType.REMOVE -> 영속성 전이
     * - 게시글 삭제 시 관련된 모든 댓글도 자동 삭제 처리 함
     * - 데이터 무결성 보장
     */
    @OrderBy("id DESC") // 정렬 옵션 설정
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = CascadeType.REMOVE)
    List<Reply> replies = new ArrayList<>(); // List 선언과 동시에 초기화


}
