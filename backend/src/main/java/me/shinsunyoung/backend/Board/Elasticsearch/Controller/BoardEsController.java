package me.shinsunyoung.backend.Board.Elasticsearch.Controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.Board.Elasticsearch.DTO.BoardEsDocument;
import me.shinsunyoung.backend.Board.Elasticsearch.Service.BoardEsService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardEsController {

    private final BoardEsService boardEsService;

    /**
     * 엘라스틱 서치 검색 결과를 page형태로 감싼 다음 HTTP 응답을 Json으로 반환
     *
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @GetMapping("elasticsearch")
    public ResponseEntity<Page<BoardEsDocument>> elasticSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ){
        return ResponseEntity.ok(boardEsService.search(keyword, page, size));
    }



}
