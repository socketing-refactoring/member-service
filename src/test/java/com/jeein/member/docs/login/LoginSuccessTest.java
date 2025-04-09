package com.jeein.member.docs.login;

import static com.jeein.member.ResponseMessage.LOGIN_SUCCESS;
import static com.jeein.member.docs.DocumentIdentifier.*;
import static com.jeein.member.docs.RestDocsUtil.doc;
import static com.jeein.member.docs.snippets.CommonSnippet.successResponseWithDataFields;
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
import com.jeein.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Transactional
@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
@DisplayName("로그인 성공 테스트")
public class LoginSuccessTest {

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
    @DisplayName("로그인 요청이 유효하고 회원가입 정보와 일치하면 로그인에 성공한다.")
    void login_withInvalidEmail_shouldReturnForbidden() throws Exception {
        LoginRequestDTO loginRequest = LoginRequestDTO.of("email@example.com", "password");
        log.debug(loginRequest.toString());

        mockMvc.perform(
                        post(ApiPath.MEMBER_LOGIN)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.message").value(LOGIN_SUCCESS))
                .andExpect(jsonPath("$.errors").doesNotExist())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("email@example.com"))
                .andExpect(jsonPath("$.data.name").value("이름"))
                .andExpect(jsonPath("$.data.nickname").value("닉네임"))
                .andDo(
                        doc(
                                LOGIN_SUCCESS_BASE,
                                MEMBER_LOGIN_REQUEST_FIELDS,
                                successResponseWithDataFields()));
    }
}
