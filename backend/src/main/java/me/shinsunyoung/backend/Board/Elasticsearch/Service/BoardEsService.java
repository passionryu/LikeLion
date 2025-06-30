package me.shinsunyoung.backend.Board.Elasticsearch.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
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

    /* Elasticsearch REST API를 타입 안전하게 호출 */
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
    /**
     * 검색 메서드
     *
     * @param keyword 검색 키워드
     * @param page
     * @param size
     * @return
     */
    public Page<BoardEsDocument> search(String keyword, int page, int size) {

        // ======================= Try =========================
        try {

            /**
             * from : 건너 뛸 문서의 수
             * page : 요청한 페이지 번호 : 현재 페이지 =0
             * size : 한 페이지에서 보여 줄 문서의 수
             */
            int from = page * size;

            /* 엘라스틱 서치에서 사용할 검색 조건을 담는 객체 */
            Query query; // 쿼리 객체의 특징은?

            /* 검색어가 없을 때, MatchAllQuery함수 -> 모든 문서 검색 */
            if (keyword == null || keyword.isBlank()) {
                /* MatchAllQuery는 엘라스틱 서치에서 조건 없이 모든 문서를 검색할 때 사용하는 쿼리 */
                query = MatchAllQuery.of(m -> m)._toQuery(); // 전체 문서를 가져오는 쿼리를 생성하는 람다 함수
            }
            /* 검색어가 있을 떄, */
            else {
                /**
                 * BoolQuery는 복수의 검색 조건을 조합할 때 사용하는 쿼리
                 */
                query = BoolQuery.of(b -> {

                    // PrefixQuery는 해당 필드가 특정 단어로 시작하는지 검사하는 쿼리
                    // MatchQuery는 해당 단어가 포함되어 있는지 검사하는 쿼리

                    /**
                     * Must
                     * should
                     * must_not
                     * filter
                     */
                    b.should(PrefixQuery.of(p -> p.field("title").value(keyword))._toQuery());
                    b.should(PrefixQuery.of(p -> p.field("content").value(keyword))._toQuery());

                    // fuzziness: "AUTO"는  오타 허용 검색 기능을 자동으로 켜주는 설정 -> 유사도 계산을 매번 수행하기 때문에 느림
                    //짧은 키워드에는 사용 xxx
                    //오타 허용 (오타허용은 match만 가능 )
                    if (keyword.length()>=3){
                        b.should(MatchQuery.of(m ->m.field("title").query(keyword).fuzziness("AUTO"))._toQuery());
                        b.should(MatchQuery.of(m ->m.field("content").query(keyword).fuzziness("AUTO"))._toQuery());
                    }

                    return b;
                })._toQuery();
            }

            // SearchRequest는 엘라스틱 서치에서 검색을 하기 위한 검색 요청 객체
            // 인덱스 명 , 페이징 정보, 쿼리를 포함한 검색 요청
            SearchRequest request = SearchRequest.of(s -> s
                    .index("board-index")
                    .from(from)
                    .size(size)
                    .query(query)
            );

            // SerachResponse는 엘라스틱서치의 검색 결과를 담고있는 응답 객체
            SearchResponse<BoardEsDocument> response =
                    // 엘라스틱 서치의 명령을 전달하는 자바 API 검색 요청으르 담아서 응답 객체로 반환
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

        }
        // ======================= Try =========================
        catch (IOException e) {
            log.error("검색 오류 : {}", e.getMessage());
            throw new RuntimeException("검색 중 오류 발생", e);
        }
    }

    /**
     * 대량 색인 : 1000개씩 나눠서 Elasticsearch에 색인
     *
     * @param documents
     * @throws IOException
     */
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
