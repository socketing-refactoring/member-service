package com.jeein.member.docs.join;

import static com.jeein.member.ResponseMessage.JOIN_SUCCESS;
import static com.jeein.member.docs.DocumentIdentifier.JOIN_SUCCESS_BASE;
import static com.jeein.member.docs.DocumentIdentifier.JOIN_SUCCESS_CASE;
import static com.jeein.member.docs.RestDocsUtil.doc;
import static com.jeein.member.docs.snippets.CommonSnippet.successResponseWithDataFields;
import static com.jeein.member.docs.snippets.MemberSnippet.MEMBER_JOIN_REQUEST_FIELDS;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeein.member.docs.ApiPath;
import com.jeein.member.dto.request.JoinRequestDTO;
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
public class JoinSuccessTest {

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
    }

    @Test
    @DisplayName("회원가입 요청이 유효하고 이메일과 닉네임이 고유하면 회원가입이 성공한다.")
    void joinMember_success() throws Exception {
        JoinRequestDTO request = JoinRequestDTO.of("email@example.com", "이름", "닉네임", "password");

        mockMvc.perform(post(ApiPath.MEMBER_JOIN).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.message").value(JOIN_SUCCESS))
                .andExpect(jsonPath("$.errors").doesNotExist())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("email@example.com"))
                .andExpect(jsonPath("$.data.name").value("이름"))
                .andExpect(jsonPath("$.data.nickname").value("닉네임"))
                .andExpect(header().string("Location", containsString("api/v1/members/")))
                .andDo(
                        doc(
                                JOIN_SUCCESS_BASE,
                                MEMBER_JOIN_REQUEST_FIELDS,
                                successResponseWithDataFields()));
    }

    @Test
    @DisplayName("회원가입 요청의 이메일과 닉네임이 탈퇴한 회원의 것과 동일해도 회원가입이 성공한다.")
    void joinWithDuplicateEmailAndNicknameOfDeleted_shouldSuccess() throws Exception {
        // given
        JoinRequestDTO request = JoinRequestDTO.of("email@example.com", "이름", "닉네임", "password");
        memberService.deleteMember(memberService.joinMember(request).getData().getId());

        // when & then
        mockMvc.perform(post(ApiPath.MEMBER_JOIN).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.message").value(JOIN_SUCCESS))
                .andExpect(jsonPath("$.errors").doesNotExist())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value("email@example.com"))
                .andExpect(jsonPath("$.data.name").value("이름"))
                .andExpect(jsonPath("$.data.nickname").value("닉네임"))
                .andExpect(header().string("Location", containsString("api/v1/members/")))
                .andDo(
                        doc(
                                JOIN_SUCCESS_CASE + "/deleted",
                                MEMBER_JOIN_REQUEST_FIELDS,
                                successResponseWithDataFields()));
    }
}
