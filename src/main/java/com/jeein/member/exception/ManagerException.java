package com.jeein.member.exception;

import lombok.Getter;

@Getter
public class ManagerException extends RuntimeException {
    private final ErrorCode errorCode;

    public ManagerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
