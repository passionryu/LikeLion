package me.shinsunyoung.backend.Board.Elasticsearch.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.PrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.shinsunyoung.backend.Board.Elasticsearch.DTO.BoardEsDocument;
import me.shinsunyoung.backend.Board.Elasticsearch.Repository.BoardEsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardEsService {

    private final ElasticsearchClient client;

    private final BoardEsRepository repository;

    // 데이터 저장 메서드
    public void save(BoardEsDocument document){
        repository.save(document);
    }

    // 데이터 삭제 메서드
    public void deleteById(String id){
        repository.deleteById(id);
    }

    // 검색 키워드와 페이지 번호와 페이지 크기를 받아서 엘라스틱서치에서 검색하는 메서드
    // 검색된 정보와 페이징 정보도 함께 반환하도록 하기 위해 page 객체를 사용하여 반환
    public Page<BoardEsDocument> search(String keyword, int page, int size) {

        try {
            int from = page * size;

            // 엘라스틱 서치에서 사용할 검색조건을 담는 객체
            Query query;

            // 검색어가 없으면 모든 문서를 검색하는 MatchAll 쿼리
            if (keyword == null || keyword.isBlank()) {
                query = MatchAllQuery.of(m -> m)._toQuery(); // 전체 문서를 가져오는 쿼리를 생성하는 람다 함수
                // MatchAllQuery는 엘라스틱 서치에서 조건 없이 모든 문서를 검색할 때 사용하는 쿼리
            }
            // 검색어가 있을 때
            else {
                // boolquery는 복수 조건을 조합할 때 사용하는 쿼리
                // 이 쿼리 안에서 여러개의 조건을 나열
                // 예를 들어서 백엔드라는 키워드가 들어왔을 때 이 백엔드 키워드를 어떻게 부넉해서 데이터를 보여줄 것인가를 작성
                query = BoolQuery.of(b -> {

                    // PrefixQuery는 해당 필드가 특정 단어로 시작하는지 검사하는 쿼리
                    // MatchQuery는 해당 단어가 포함되어 있는지 검사하는 쿼리

                    /**
                     * Must
                     * should
                     * must_not
                     * filter
                     *
                     */
                    b.should(PrefixQuery.of(p -> p.field("title").value(keyword))._toQuery());
                    b.should(PrefixQuery.of(p -> p.field("content").value(keyword))._toQuery());

                    return b;
                })._toQuery();
            }

            //  SearchRequest는 엘라스틱 서치에서 검색을 하기 위한 검색 요청 객체
            // 인덱스 명 , 페이징 정보, ㄴ쿼리를 포함한 검색 요청
            SearchRequest request = SearchRequest.of(s -> s
                    .index("board-index")
                    .from(from)
                    .size(size)
                    .query(query)
            );

            // SerachResponse는 엘라스틱서치의 검색 결과를 담고있는 응답 객체
            SearchResponse<BoardEsDocument> response =
                    // 엘라스틱 서치의 명령을 전달하는 자바 API 검색 요청ㅇ르 담아서 응답 객체로 반환
                    client.search(request, BoardEsDocument.class);

            // 위 응답 객체에서 받은 검색 결과 중 문서만 추출해서 리스트로 만듬
            // Hit는 엘라스틱 서치에서 검색된 문서 1개를 감싸고 있는 객체
            List<BoardEsDocument> content = response.hits() // 엘라스틱 서치 응답에서 hits(문서 검색 결과) 전체를 꺼냄
                    .hits() // 검색 결과 안에 개별 리스트를 가져옴
                    .stream() // 자바 Stream api를 사용
                    .map(Hit::source) // 각 hti 객체에서 실제 문서를 꺼내는 작업
                    .collect(Collectors.toList()); // 위에서 꺼낸 객체를 자바 List에 넣음

            // 전체 검색 결과 수 (총 문서의 갯수)
            long total = response.hits().total().value();

            // PageImpl 객체를 사용해서 Spring 에서 사용할 수 있는 Page 객체로 반환
            return new PageImpl<>(content, PageRequest.of(page, size), total);

        } catch (IOException e) {
            log.error("검색 오류 : {}", e.getMessage());
            throw new RuntimeException("검색 중 오류 발생", e);
        }
    }

    public void bulkIndexInsert(List<BoardEsDocument> documents) throws IOException {

        int batchSize =1000;

        for(int i=0 ; i < documents.size() ; i +=batchSize){
            int end = Math.min(i + batchSize, documents.size());

            List<BoardEsDocument> batch = documents.subList(i,end);

            BulkRequest.Builder br = new BulkRequest.Builder();

            // 각 문서를 bulk 요청 안에 하나씩 담음
            for(BoardEsDocument document: batch){
                br.operations(op->op
                        .index(idx->idx
                                .index("board-index") // 인덱스 명
                                .id(String.valueOf(document.getId()))
                                .document(document)
                        )
                );
            }
            // bulk 요청 실행 : batch 단위로 엘라스틱 서치에 색인 수행
            BulkResponse response = client.bulk(br.build());
            // 벌크 작업 중 에러가 있는 경우 로그 출력
            if(response.errors()){
                for(BulkResponseItem item : response.items()){
                    if(item.error() != null){
                        // 실패한 문서의 ID와 에러 내용을 출력
                        log.error("엘라스틱 서치 벌크 색인 작업 중 오류 실패 ID : {}, 오류 : {}",item.id(), item.error());
                    }
                }
            }
        }
    }

}
