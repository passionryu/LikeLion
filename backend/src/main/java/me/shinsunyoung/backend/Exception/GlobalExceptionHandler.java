package me.shinsunyoung.backend.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@Slf4j
/**
 * Spring 컨트롤러에서 발생하는 예외 중앙 처리
 *
 * 어노테이션 @ControllerAdvice와 @ResponseBody를 결합한 형태
 * 컨트롤러에서 발생하는 예외를 처리하고 JSON 응답을 반환.
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 컨트롤러에서 RuntimeException 에러가 발생했을 때, 이 메서드가 대신 처리하도록 매핑
     *
     * @param e Runtime 예외
     * @return 직접 커스텀한 ErrorResponse
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e){

        ErrorResponse errorResponse = new ErrorResponse(
                600, // 내가 커스텀 가능
                "런타임 에러 발생",
                e.getMessage()
        );

        log.error(errorResponse.toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 400: 파라미터 타입 오류, JSON 파싱 오류 등
    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
                      HttpMessageNotReadableException.class,
                      MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {

        log.warn("[BAD_REQUEST] {}", e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                601,
                "파라미터 타입 오류 발생",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 400: DTO validation(@Valid) 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {

        log.warn("[VALIDATION_FAIL] {}", e.getMessage());

        //유효성 검증 실패한 모든 필드 오류 리스트를 가져옴
        //유효성 검증 실패한 필드명과 이유를 콤마로 연결해서 한 줄 메시지로 만들어줌
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((m1, m2) -> m1 + ", " + m2)
                .orElse("유효성 검사 실패");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }

    //인증 실패  401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException e) {

        log.warn("[LOGIN_FAIL] {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    // 403: 인가(권한) 실패
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {

        log.warn("[ACCESS_DENIED] {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
    }

    //그 외 모든 예외 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {

        log.error("[EXCEPTION][UNHANDLED] ", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
    }


}
