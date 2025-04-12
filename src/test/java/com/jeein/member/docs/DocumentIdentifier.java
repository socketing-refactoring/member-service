package com.jeein.member.docs;

public class DocumentIdentifier {
    public static final String JOIN = "join";
    public static final String JOIN_SUCCESS = JOIN + "/success";
    public static final String JOIN_SUCCESS_BASE = JOIN_SUCCESS + "/base";
    public static final String JOIN_SUCCESS_CASE = JOIN_SUCCESS + "/case";

    public static final String JOIN_VALIDATION = JOIN + "/validation";
    public static final String JOIN_VALIDATION_EMAIL = JOIN_VALIDATION + "/email";
    public static final String JOIN_VALIDATION_NAME = JOIN_VALIDATION + "/name";
    public static final String JOIN_VALIDATION_NICKNAME = JOIN_VALIDATION + "/nickname";
    public static final String JOIN_VALIDATION_PASSWORD = JOIN_VALIDATION + "/password";

    public static final String JOIN_DUPLICATION = JOIN + "/duplication";
    public static final String JOIN_DUPLICATION_EMAIL = JOIN_DUPLICATION + "/email";
    public static final String JOIN_DUPLICATION_NICKNAME = JOIN_DUPLICATION + "/nickname";

    public static final String LOGIN = "login";
    public static final String LOGIN_SUCCESS = LOGIN + "/success";
    public static final String LOGIN_SUCCESS_BASE = LOGIN_SUCCESS + "/base";

    public static final String LOGIN_VALIDATION = LOGIN + "/validation";
    public static final String LOGIN_VALIDATION_EMAIL = LOGIN_VALIDATION + "/email";
    public static final String LOGIN_VALIDATION_PASSWORD = LOGIN_VALIDATION + "/password";

    public static final String LOGIN_MISMATCH = LOGIN + "/mismatch";
    public static final String LOGIN_MISMATCH_EMAIL = LOGIN_MISMATCH + "/email";
    public static final String LOGIN_MISMATCH_PASSWORD = LOGIN_MISMATCH + "/password";
}
