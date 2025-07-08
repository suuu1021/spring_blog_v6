package com.tenco.blog.board;

import com.tenco.blog._core.common.PageLink;
import com.tenco.blog._core.errors.exception.Exception403;
import com.tenco.blog.user.User;
import com.tenco.blog.utils.Define;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private static final Logger log = LoggerFactory.getLogger(BoardController.class);
    private final BoardService boardService;

    /**
     * 게시글 수정 화면 요청
     */
    @GetMapping("/board/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long boardId,
                             HttpServletRequest request, HttpSession session) {
        // 인증, 권한
        User sessionUSer = (User) session.getAttribute(Define.SESSION_USER);
        boardService.checkBoardOwner(boardId, sessionUSer.getId());
        request.setAttribute("board", boardService.findById(boardId));
        return "board/update-form";
    }


    @PostMapping("/board/{id}/update-form")
    public String update(@PathVariable(name = "id") Long id,
                         BoardRequest.UpdateDTO reqDTO,
                         HttpSession session) {
        // 1. 인증 검사
        // 2. 데이터 유효성 검사
        // 3. 수정 요청 Service 위임
        // 4. 리다이렉트 처리
        reqDTO.validate();
        User sessionUser = (User) session.getAttribute("sessionUser");
        boardService.updateById(id, reqDTO, sessionUser);
        return "redirect:/board/" + id;
    }


    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable(name = "id") Long id, HttpSession session) {
        // 1. 인증 검사
        // 2. 세션에서 로그인한 사용자 정보 추출
        // 3. Service 위임
        // 4. 메인 페이지로 리다이렉트 처리
        User sessionUser = (User) session.getAttribute("sessionUser");
        boardService.deleteById(id, sessionUser);
        return "redirect:/";
    }


    @GetMapping("/board/save-form")
    public String saveForm() {
        log.info("게시글 작성 화면 요청");
        return "board/save-form";
    }


    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO reqDTO, HttpSession session) {
        // 1. 인증 검사
        // 2. 유효성 검사
        // 3. Service 위임
        reqDTO.validate();
        User sessionUser = (User) session.getAttribute("sessionUser");
        boardService.save(reqDTO, sessionUser);
        return "redirect:/";
    }


    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        @RequestParam(name = "size", defaultValue = "3") int size) {
        //                            현재 페이지 번호, 한 페이지 당 게시글 개수, 내림차순
        Pageable pageable = PageRequest.of(page -1, size, Sort.by("id").descending());
        Page<Board> boardPage = boardService.findAll(pageable);

        // 페이지 네비게이션 용 데이터 준비
        List<PageLink> pageLinks = new ArrayList<>();
        for (int i =0; i < boardPage.getTotalPages(); i++) {
            pageLinks.add(new PageLink(i, i+1, i == boardPage.getNumber()));
        }
        Integer previousPageNumber = boardPage.hasPrevious() ? boardPage.getNumber() : null;
        Integer nextPageNumber = boardPage.hasNext() ? boardPage.getNumber() + 2 : null;

        // 뷰 화면에 데이터 전달
        model.addAttribute("boardPage", boardPage);

        // 페이지 네비게이션에 사용할 번호 링크 리스트
        model.addAttribute("pageLinks", pageLinks);

        // 이전 페이지 번호 (없으면 null)
        model.addAttribute("previousPageNumber",previousPageNumber);
        // 다음 페이지 번호 (없으면 null)
        model.addAttribute("nextPageNumber", nextPageNumber);

        return "index";
    }


    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model, HttpSession session) {
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        Board board = boardService.findByIdWithReplies(id,sessionUser);
        model.addAttribute("board", board);
        return "board/detail";
    }
}

