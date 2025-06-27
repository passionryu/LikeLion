package me.shinsunyoung.backend.Exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private int code; //상태 코드
    private String message; //커스텀 예러 메시지
    private String detail; // 상세 에러 메시지

}
