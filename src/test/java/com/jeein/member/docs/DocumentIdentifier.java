package com.jeein.member.docs;

public class DocumentIdentifier {
    public static final String JOIN = "join";
    public static final String JOIN_SUCCESS = JOIN + "/success";
    public static final String JOIN_SUCCESS_BASE = JOIN_SUCCESS + "/base";
    public static final String JOIN_SUCCESS_CASE = JOIN_SUCCESS + "/cases";

    public static final String JOIN_VALIDATION = JOIN + "/validation";
    public static final String JOIN_VALIDATION_EMAIL = JOIN_VALIDATION + "/email";
    public static final String JOIN_VALIDATION_NAME = JOIN_VALIDATION + "/name";
    public static final String JOIN_VALIDATION_NICKNAME = JOIN_VALIDATION + "/nickname";
    public static final String JOIN_VALIDATION_PASSWORD = JOIN_VALIDATION + "/password";

    public static final String JOIN_EXCEPTION = JOIN + "/exception";
    public static final String JOIN_EXCEPTION_DUPLICATION_EMAIL = JOIN_EXCEPTION + "/email";
    public static final String JOIN_EXCEPTION_DUPLICATION_NICKNAME = JOIN_EXCEPTION + "/nickname";

    public static final String LOGIN = "login";
    public static final String LOGIN_SUCCESS = LOGIN + "/success";
    public static final String LOGIN_SUCCESS_BASE = LOGIN_SUCCESS + "/base";

    public static final String LOGIN_VALIDATION = LOGIN + "/validation";
    public static final String LOGIN_VALIDATION_EMAIL = LOGIN_VALIDATION + "/email";
    public static final String LOGIN_VALIDATION_PASSWORD = LOGIN_VALIDATION + "/password";

    public static final String LOGIN_EXCEPTION = LOGIN + "/exception";
    public static final String LOGIN_EXCEPTION_INVALID_EMAIL = LOGIN_EXCEPTION + "/email";
    public static final String LOGIN_EXCEPTION_INVALID_PASSWORD = LOGIN_EXCEPTION + "/password";
}
