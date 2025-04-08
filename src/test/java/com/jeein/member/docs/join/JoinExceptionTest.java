package com.jeein.member.docs.join;

import static com.jeein.member.docs.DocumentIdentifier.JOIN_EXCEPTION_DUPLICATION_EMAIL;
import static com.jeein.member.docs.DocumentIdentifier.JOIN_EXCEPTION_DUPLICATION_NICKNAME;
import static com.jeein.member.docs.RestDocsUtil.doc;
import static com.jeein.member.docs.snippets.CommonSnippet.errorCodeOnlyResponseFields;
import static com.jeein.member.docs.snippets.MemberSnippet.MEMBER_JOIN_REQUEST_FIELDS;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeein.member.docs.ApiPath;
import com.jeein.member.dto.request.JoinRequestDTO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@ExtendWith(RestDocumentationExtension.class)
@DisplayName("회원가입 실패 예외 테스트")
public class JoinExceptionTest {

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
    @DisplayName("회원가입 요청 이메일이 이미 존재하면 409 에러와 메시지를 반환한다.")
    void joinMember_emailAlreadyExists() throws Exception {
        JoinRequestDTO duplicateRequest =
                JoinRequestDTO.of("email@example.com", "새 이름", "새 닉네임", "password");

        ErrorCode errorCode = ErrorCode.ALREADY_EXISTING_EMAIL;
        mockMvc.perform(
                        post(ApiPath.MEMBER_JOIN)
                                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(errorCode.getCode()))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.errors").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(
                        doc(
                                JOIN_EXCEPTION_DUPLICATION_EMAIL,
                                MEMBER_JOIN_REQUEST_FIELDS,
                                errorCodeOnlyResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 닉네임이 이미 존재하면 409 에러와 메시지를 반환한다.")
    void joinMember_nicknameAlreadyExists() throws Exception {
        JoinRequestDTO duplicateRequest =
                JoinRequestDTO.of("another@example.com", "이름", "닉네임", "password");

        ErrorCode errorCode = ErrorCode.ALREADY_EXISTING_NICKNAME;
        mockMvc.perform(
                        post(ApiPath.MEMBER_JOIN)
                                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(errorCode.getCode()))
                .andExpect(jsonPath("$.message").value(errorCode.getMessage()))
                .andExpect(jsonPath("$.errors").doesNotExist())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(
                        doc(
                                JOIN_EXCEPTION_DUPLICATION_NICKNAME,
                                MEMBER_JOIN_REQUEST_FIELDS,
                                errorCodeOnlyResponseFields()));
    }
}
