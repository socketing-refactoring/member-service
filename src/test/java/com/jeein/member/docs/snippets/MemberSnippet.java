package com.jeein.member.docs.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class MemberSnippet {

public static final RequestFieldsSnippet MEMBER_JOIN_REQUEST_FIELDS = requestFields(
            fieldWithPath("email").description("이메일"),
            fieldWithPath("name").description("이름"),
            fieldWithPath("nickname").optional().description("닉네임"),
            fieldWithPath("password").description("비밀번호")
    );

    public static final RequestFieldsSnippet MEMBER_LOGIN_REQUEST_FIELDS = requestFields(
            fieldWithPath("email").description("이메일"),
            fieldWithPath("password").description("비밀번호")
    );
}
