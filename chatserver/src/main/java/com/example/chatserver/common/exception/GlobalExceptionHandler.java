package com.example.chatserver.common.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // @ControllerAdvice + @ResponseBody, REST API의 예외를 전역적으로 처리
public class GlobalExceptionHandler {
    
    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex,
        HttpServletRequest request) {
        log.warn("Resource not found at URI [{}]: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(), // 서비스에서 던진 메시지를 그대로 사용
            request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 다양한 IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
        HttpServletRequest request) {
        String message = ex.getMessage();
        HttpStatus status;
        String errorPhrase;

        // 예외 메시지 내용에 따라 HTTP 상태 코드 분기 (더 좋은 방법은 커스텀 예외 사용)
        if ("이미 존재하는 이메일 입니다.".equals(message)) {
            status = HttpStatus.CONFLICT; // 409
            log.warn("Conflict (duplicate resource) at URI [{}]: {}", request.getRequestURI(),
                message);
        } else if ("비밀번호가 일치하지 않습니다.".equals(message)) {
            status = HttpStatus.BAD_REQUEST; // 400 (잘못된 자격 증명으로 인한 로그인 실패)
            log.warn("Bad request (invalid credentials) at URI [{}]: {}", request.getRequestURI(),
                message);
        } else if ("cannot access".equals(message) || "cannot delete".equals(message)) {
            status = HttpStatus.FORBIDDEN; // 403 (해당 작업에 대한 권한 없음)
            log.warn("Forbidden operation at URI [{}]: {}", request.getRequestURI(), message);
        } else {
            // 그 외 일반적인 잘못된 인자
            status = HttpStatus.BAD_REQUEST; // 400
            log.warn("Illegal argument/Bad request at URI [{}]: {}", request.getRequestURI(),
                message);
        }
        errorPhrase = status.getReasonPhrase();

        ErrorResponse errorResponse = new ErrorResponse(
            status.value(),
            errorPhrase,
            message,
            request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    // 500 Internal Server Error: 예상치 못한 모든 서버 내부 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex,
        HttpServletRequest request) {
        log.error("Unexpected internal server error occurred at URI [{}]: {}",
            request.getRequestURI(), ex.getMessage(), ex); // 스택 트레이스 포함
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "서버 내부 오류가 발생했습니다. 문제가 지속되면 관리자에게 문의해 주세요.", // 클라이언트에게는 일반적인 메시지
            request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 필요에 따라 Spring Security의 AccessDeniedException 등 다른 특정 예외 핸들러 추가 가능
    // @ExceptionHandler(AccessDeniedException.class)
    // public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
    //     log.warn("Access denied at URI [{}]: {}", request.getRequestURI(), ex.getMessage());
    //     ErrorResponse errorResponse = new ErrorResponse(
    //             HttpStatus.FORBIDDEN.value(),
    //             HttpStatus.FORBIDDEN.getReasonPhrase(),
    //             "요청한 리소스에 접근할 권한이 없습니다.",
    //             request.getRequestURI()
    //     );
    //     return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    // }
}
