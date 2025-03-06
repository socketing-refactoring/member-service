package com.jeein.member.exception;

import com.jeein.member.dto.common.CommonResponseDTO;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnknownException.class)
    protected ResponseEntity<CommonResponseDTO<Object>> handleUnknownException(UnknownException e) {
        CommonResponseDTO<Object> response =
                CommonResponseDTO.error(ErrorCode.INTERNAL_SERVER_ERROR, new ArrayList<>());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<CommonResponseDTO<Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        CommonResponseDTO<Object> response =
                CommonResponseDTO.error(ErrorCode.INVALID_REQUEST_VALUE, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<CommonResponseDTO<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        CommonResponseDTO<Object> response = CommonResponseDTO.error(e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MemberException.class)
    protected ResponseEntity<CommonResponseDTO<Object>> handleMemberException(MemberException e) {
        CommonResponseDTO<Object> response =
                CommonResponseDTO.error(e.getErrorCode(), new ArrayList<>());
        return new ResponseEntity<>(response, e.getErrorCode().getStatus());
    }
}
