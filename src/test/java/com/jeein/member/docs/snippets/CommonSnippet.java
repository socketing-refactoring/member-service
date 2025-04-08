package com.jeein.member.docs.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class CommonSnippet {
    public static final FieldDescriptor ERROR_CODE = fieldWithPath("code").description("에러 코드");
    public static final FieldDescriptor ERROR_MESSAGE =
            fieldWithPath("message").description("에러 메시지");
    public static final FieldDescriptor ERRORS =
            fieldWithPath("errors").description("유효성 검사 실패 필드 목록").type("array").optional();
    public static final FieldDescriptor ERROR_FIELD =
            fieldWithPath("errors[].field").description("필드");
    public static final FieldDescriptor ERROR_VALUE =
            fieldWithPath("errors[].value").description("잘못된 값");
    public static final FieldDescriptor ERROR_REASON =
            fieldWithPath("errors[].reason").description("이유");

    public static final FieldDescriptor SUCCESS_CODE = fieldWithPath("code").description("성공 코드");
    public static final FieldDescriptor SUCCESS_MESSAGE =
            fieldWithPath("message").description("성공 메시지");
    public static final FieldDescriptor SUCCESS_DATA =
            fieldWithPath("data").optional().description("응답 값");
    public static final FieldDescriptor DATA_ID = fieldWithPath("data.id").description("회원 ID");
    public static final FieldDescriptor DATA_NAME = fieldWithPath("data.name").description("회원 이름");
    public static final FieldDescriptor DATA_NICKNAME =
            fieldWithPath("data.nickname").description("회원 닉네임");
    public static final FieldDescriptor DATA_EMAIL =
            fieldWithPath("data.email").description("회원 이메일");

    public static final FieldDescriptor SUCCESS_DATA_ARRAY =
            fieldWithPath("data").optional().description("응답 값");
    public static final FieldDescriptor DATA_ARRAY_ID =
            fieldWithPath("data.id").description("회원 ID");
    public static final FieldDescriptor DATA_ARRAY_NAME =
            fieldWithPath("data.name").description("회원 이름");
    public static final FieldDescriptor DATA_ARRAY_NICKNAME =
            fieldWithPath("data.nickname").description("회원 닉네임");
    public static final FieldDescriptor DATA_ARRAY_EMAIL =
            fieldWithPath("data.email").description("회원 이메일");

    public static ResponseFieldsSnippet errorCodeOnlyResponseFields() {
        return responseFields(ERROR_CODE, ERROR_MESSAGE);
    }

    public static ResponseFieldsSnippet errorResponseFields() {
        return responseFields(
                ERROR_CODE, ERROR_MESSAGE, ERRORS, ERROR_FIELD, ERROR_VALUE, ERROR_REASON);
    }

    public static ResponseFieldsSnippet successCodeOnlyResponseFields() {
        return responseFields(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public static ResponseFieldsSnippet successResponseFields() {
        return responseFields(SUCCESS_CODE, SUCCESS_MESSAGE, SUCCESS_DATA);
    }

    public static ResponseFieldsSnippet successResponseWithDataFields() {
        return responseFields(
                SUCCESS_CODE,
                SUCCESS_MESSAGE,
                SUCCESS_DATA,
                DATA_ID,
                DATA_NAME,
                DATA_NICKNAME,
                DATA_EMAIL);
    }
}
