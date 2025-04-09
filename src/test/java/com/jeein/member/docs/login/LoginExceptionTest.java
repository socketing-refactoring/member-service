package com.jeein.member.docs.login;

import static com.jeein.member.docs.DocumentIdentifier.*;
import static com.jeein.member.docs.RestDocsUtil.doc;
import static com.jeein.member.docs.snippets.CommonSnippet.errorCodeOnlyResponseFields;
import static com.jeein.member.docs.snippets.MemberSnippet.MEMBER_LOGIN_REQUEST_FIELDS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeein.member.docs.ApiPath;
import com.jeein.member.dto.request.JoinRequestDTO;
import com.jeein.member.dto.request.LoginRequestDTO;
import com.jeein.member.exception.ErrorCode;
import com.jeein.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ExtendWith(RestDocumentationExtension.class)
@DisplayName("로그인 실패 예외 테스트")
public class LoginExceptionTest {

    @Autowired private WebApplicationContext context;

    @Autowired private MemberService memberService;

    @Autowired private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(
            WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentation) {
        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .apply(documentationConfiguration(restDocumentation))
                        .defaultRequest(
                                get("/").accept(MediaType.APPLICATION_JSON)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .build();

        // 기존 회원 생성
        JoinRequestDTO request = JoinRequestDTO.of("email@example.com", "이름", "닉네임", "password");
        memberService.joinMember(request);
    }

    @Test
    @DisplayName("로그인 요청 이메일을 찾을 수 없으면 403 에러와 메시지를 반환한다.")
    void login_withInvalidEmail_shouldReturnForbidden() throws Exception {
        LoginRequestDTO loginRequest = LoginRequestDTO.of("invalid_email@example.com", "password");

        ErrorCode errorCode = ErrorCode.INVALID_MEMBER;
        mockMvc.perform(
                        post(ApiPath.MEMBER_LOGIN)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(errorCode.getCode()))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.errors").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(
                        doc(
                                LOGIN_EXCEPTION_INVALID_EMAIL,
                                MEMBER_LOGIN_REQUEST_FIELDS,
                                errorCodeOnlyResponseFields()));
    }

    @Test
    @DisplayName("로그인 요청 비밀번호가 불일치하면 403 에러와 메시지를 반환한다.")
    void login_withInvalidPassword_shouldReturnForbidden() throws Exception {
        LoginRequestDTO loginRequest = LoginRequestDTO.of("email@example.com", "wrong_password");

        ErrorCode errorCode = ErrorCode.INVALID_PASSWORD;
        mockMvc.perform(
                        post(ApiPath.MEMBER_LOGIN)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(errorCode.getCode()))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.errors").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(
                        doc(
                                LOGIN_EXCEPTION_INVALID_PASSWORD,
                                MEMBER_LOGIN_REQUEST_FIELDS,
                                errorCodeOnlyResponseFields()));
    }
}
