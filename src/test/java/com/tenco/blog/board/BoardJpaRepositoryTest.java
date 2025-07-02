package com.tenco.blog.board;

import com.tenco.blog.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class BoardJpaRepositoryTest {

    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    public void save_test() {
        User testUser = User.builder()
                .username("테스트 유저")
                .password("1234")
                .email("a@naver.com")
                .build();

        System.out.println("user_id : " + testUser.getId());
        em.persistAndFlush(testUser); // 즉시 사용자 저장
        System.out.println("user_id : " + testUser.getId());

        Board board = Board.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .user(testUser)
                .build();

        boardJpaRepository.save(board);
    }


}
