package com.jeein.member.exception;

import lombok.Getter;

@Getter
public class UnknownException extends RuntimeException {
    private final ErrorCode errorCode;

    public UnknownException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
