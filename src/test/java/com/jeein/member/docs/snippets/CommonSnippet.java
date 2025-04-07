package com.jeein.member.docs.snippets;

import org.springframework.restdocs.payload.ResponseFieldsSnippet;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public class CommonSnippet {

    public static ResponseFieldsSnippet errorResponseFields() {
        return responseFields(
                fieldWithPath("code").description("에러 코드"),
                fieldWithPath("message").description("에러 메시지"),
                fieldWithPath("errors").optional().description("유효성 검사 실패 필드 목록"),
                fieldWithPath("errors[].field").description("필드"),
                fieldWithPath("errors[].value").description("잘못된 값"),
                fieldWithPath("errors[].reason").description("이유")
        );
    }

    public static ResponseFieldsSnippet successResponseFields() {
        return responseFields(
                fieldWithPath("code").description("성공 코드"),
                fieldWithPath("message").description("성공 메시지"),
                fieldWithPath("data").optional().description("응답 값")
        );
    }
}
