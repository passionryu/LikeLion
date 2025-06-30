package me.shinsunyoung.backend.Board.Elasticsearch.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.shinsunyoung.backend.Board.DTO.BoardDTO;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "board-index")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEsDocument {

    @Id
    private String id;
    private String title;
    private String content;
    private Long userid;
    private String username;
    private String created_date;
    private String updated_date;

    public static BoardEsDocument from(BoardDTO dto){
        return BoardEsDocument.builder()
                .id(String.valueOf(dto.getId()))
                .title(dto.getTitle())
                .content(dto.getContent())
                .username(dto.getUsername())
                .userid(dto.getUser_id())
                .created_date((dto.getCreated_date()!=null ? dto.getCreated_date().toString() : null))
                        .updated_date(dto.getUpdated_date() != null? dto.getUpdated_date().toString() : null)
                        .build();

    }

}
