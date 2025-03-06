package com.jeein.member.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeein.member.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommonResponseDTO<T> {
    private String message;
    private String code;
    private List<FieldError> errors;
    private T data;

    private CommonResponseDTO(String message, String code, T data) {
        this.message = message;
        this.code = code;
        this.errors = new ArrayList<>();
        this.data = data;
    }

    private CommonResponseDTO(String message, String code, List<FieldError> errors) {
        this.message = message;
        this.code = code;
        this.errors = errors;
        this.data = null;
    }

    public static <T> CommonResponseDTO<T> success(String message, String code, T data) {
        return new CommonResponseDTO<>(message, code, data);
    }

    public static CommonResponseDTO<Object> error(ErrorCode errorCode, List<FieldError> errors) {
        return new CommonResponseDTO<>(errorCode.getMessage(), errorCode.getCode(), errors);
    }

    public static CommonResponseDTO<Object> error(
            ErrorCode errorCode, BindingResult bindingResult) {
        List<FieldError> fieldErrors = FieldError.of(bindingResult);
        return new CommonResponseDTO<>(errorCode.getMessage(), errorCode.getCode(), fieldErrors);
    }

    public static CommonResponseDTO<Object> error(MethodArgumentTypeMismatchException e) {
        String value = Optional.ofNullable(e.getValue()).map(Object::toString).orElse("");
        List<FieldError> errors = FieldError.of(e.getName(), value, e.getErrorCode());
        return new CommonResponseDTO<>(
                ErrorCode.INVALID_TYPE_VALUE.getMessage(),
                ErrorCode.INVALID_TYPE_VALUE.getCode(),
                errors);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(String field, String value, String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(BindingResult bindingResult) {
            List<org.springframework.validation.FieldError> fieldErrors =
                    bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(
                            error ->
                                    new FieldError(
                                            error.getField(),
                                            error.getRejectedValue() == null
                                                    ? ""
                                                    : error.getRejectedValue().toString(),
                                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}
