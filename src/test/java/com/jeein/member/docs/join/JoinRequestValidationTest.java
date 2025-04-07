package com.jeein.member.docs.join;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeein.member.docs.ApiPath;
import com.jeein.member.dto.request.JoinRequestDTO;
import com.jeein.member.exception.ErrorCode;
import com.jeein.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static com.jeein.member.docs.DocumentIdentifier.*;
import static com.jeein.member.docs.RestDocsUtil.doc;
import static com.jeein.member.docs.snippets.CommonSnippet.errorResponseFields;
import static com.jeein.member.docs.snippets.MemberSnippet.MEMBER_JOIN_REQUEST_FIELDS;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
@Import(JoinRequestValidationTest.TestMockConfig.class)
public class JoinRequestValidationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .defaultRequest(get("/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .build();
    }

    @TestConfiguration
    static class TestMockConfig {
        @Bean
        public MemberService memberService() {
            return Mockito.mock(MemberService.class);
        }
    }

    private Map<String, String> createValidJoinRequestMap() {
        JoinRequestDTO validRequestDto = JoinRequestDTO.of("test@example.com", "홍길동", "길동이", "password123");
        return objectMapper.convertValue(validRequestDto, new TypeReference<>() {});
    }

    private void expectInvalidField(ResultActions result, String fieldName) throws Exception {
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_REQUEST_VALUE.getCode()))
                .andExpect(jsonPath("$.message").value(containsString(ErrorCode.INVALID_REQUEST_VALUE.getMessage())))
                .andExpect(jsonPath("$.errors[0].field").value(fieldName));
    }

    @Test
    @DisplayName("회원가입 요청 시 이메일 필드가 패턴이 유효하지 않으면 400 에러 응답을 반환한다.")
    void join_withInvalidPatternEmail_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidEmailRequestMap = createValidJoinRequestMap();
        invalidEmailRequestMap.put("email", "invalid-email");

        String invalidEmailRequestJson = objectMapper.writeValueAsString(invalidEmailRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidEmailRequestJson));

        expectInvalidField(result, "email");
        result.andDo(doc(JOIN_VALIDATION_EMAIL + "/pattern", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 이메일 필드가 빈 문자열이면 400 에러 응답을 반환한다.")
    void join_withBlankEmail_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidEmailRequestMap = createValidJoinRequestMap();
        invalidEmailRequestMap.put("email", "");

        String invalidEmailRequestJson = objectMapper.writeValueAsString(invalidEmailRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidEmailRequestJson));

        expectInvalidField(result, "email");
        result.andDo(doc(JOIN_VALIDATION_EMAIL + "/blank", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 이메일 필드가 null이면 400 에러 응답을 반환한다.")
    void join_withNULLEmail_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidEmailRequestMap = createValidJoinRequestMap();
        invalidEmailRequestMap.put("email", null);

        String invalidEmailRequestJson = objectMapper.writeValueAsString(invalidEmailRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidEmailRequestJson));

        expectInvalidField(result, "email");
        result.andDo(doc(JOIN_VALIDATION_EMAIL + "/null", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 이메일 필드가 50자를 초과하면 400 에러 응답을 반환한다.")
    void join_withTooLongEmail_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidEmailRequestMap = createValidJoinRequestMap();
        invalidEmailRequestMap.put("email", "a".repeat(40) + "@example.com");

        String invalidEmailRequestJson = objectMapper.writeValueAsString(invalidEmailRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidEmailRequestJson));

        expectInvalidField(result, "email");
        result.andDo(doc(JOIN_VALIDATION_EMAIL + "/size", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 이름 필드가 빈 문자열이면 400 에러 응답을 반환한다.")
    void join_withBlankName_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidNameRequestMap = createValidJoinRequestMap();
        invalidNameRequestMap.put("name", "");

        String invalidNameRequestJson = objectMapper.writeValueAsString(invalidNameRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidNameRequestJson));

        expectInvalidField(result, "name");
        result.andDo(doc(JOIN_VALIDATION_NAME + "/blank", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 이름 필드가 null이면 400 에러 응답을 반환한다.")
    void join_withNullName_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidNameRequestMap = createValidJoinRequestMap();
        invalidNameRequestMap.put("name", null);

        String invalidNameRequestJson = objectMapper.writeValueAsString(invalidNameRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidNameRequestJson));

        expectInvalidField(result, "name");
        result.andDo(doc(JOIN_VALIDATION_NAME + "/null", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 이름 필드가 20자를 초과하면 400 에러 응답을 반환한다.")
    void join_withTooLongName_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidNameRequestMap = createValidJoinRequestMap();
        invalidNameRequestMap.put("name", "a".repeat(21));

        String invalidNameRequestJson = objectMapper.writeValueAsString(invalidNameRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidNameRequestJson));

        expectInvalidField(result, "name");
        result.andDo(doc(JOIN_VALIDATION_NAME + "/size", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 닉네임 필드가 빈 문자열이면 400 에러 응답을 반환한다.")
    void join_withBlankNickname_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidNicknameRequestMap = createValidJoinRequestMap();
        invalidNicknameRequestMap.put("nickname", "");

        String invalidNicknameRequestJson = objectMapper.writeValueAsString(invalidNicknameRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidNicknameRequestJson));

        expectInvalidField(result, "nickname");
        result.andDo(doc(JOIN_VALIDATION_NICKNAME + "/blank", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 닉네임 필드가 null이면 400 에러 응답을 반환한다.")
    void join_withNullNickname_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidNicknameRequestMap = createValidJoinRequestMap();
        invalidNicknameRequestMap.put("nickname", null);

        String invalidNicknameRequestJson = objectMapper.writeValueAsString(invalidNicknameRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidNicknameRequestJson));

        expectInvalidField(result, "nickname");
        result.andDo(doc(JOIN_VALIDATION_NICKNAME + "/null", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 닉네임 필드가 20자를 초과하면 400 에러 응답을 반환한다.")
    void join_withTooLongNickname_shouldReturnBadRequest() throws Exception {
            Map<String, String> invalidNicknameRequestMap = createValidJoinRequestMap();
            invalidNicknameRequestMap.put("nickname", "a".repeat(21));

            String invalidNicknameRequestJson = objectMapper.writeValueAsString(invalidNicknameRequestMap);
            ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                    .content(invalidNicknameRequestJson));

            expectInvalidField(result, "nickname");
            result.andDo(doc(JOIN_VALIDATION_NICKNAME + "/size", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 비밀번호 필드가 빈 문자열이면 400 에러 응답을 반환한다.")
    void join_withBlankPassword_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidPasswordRequestMap = createValidJoinRequestMap();
        invalidPasswordRequestMap.put("password", "");

        String invalidNameRequestJson = objectMapper.writeValueAsString(invalidPasswordRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidNameRequestJson));

        expectInvalidField(result, "password");
        result.andDo(doc(JOIN_VALIDATION_PASSWORD + "/blank", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 비밀번호 필드가 null이면 400 에러 응답을 반환한다.")
    void join_withNullPassword_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidPasswordRequestMap = createValidJoinRequestMap();
        invalidPasswordRequestMap.put("password", null);

        String invalidNameRequestJson = objectMapper.writeValueAsString(invalidPasswordRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidNameRequestJson));

        expectInvalidField(result, "password");
        result.andDo(doc(JOIN_VALIDATION_PASSWORD + "/null", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }

    @Test
    @DisplayName("회원가입 요청 시 비밀번호 필드가 20자를 초과하면 400 에러 응답을 반환한다.")
    void join_withTooLongPassword_shouldReturnBadRequest() throws Exception {
        Map<String, String> invalidPasswordRequestMap = createValidJoinRequestMap();
        invalidPasswordRequestMap.put("password", "a".repeat(21));

        String invalidNameRequestJson = objectMapper.writeValueAsString(invalidPasswordRequestMap);
        ResultActions result = mockMvc.perform(post(ApiPath.MEMBER_JOIN)
                .content(invalidNameRequestJson));

        expectInvalidField(result, "password");
        result.andDo(doc(JOIN_VALIDATION_PASSWORD + "/size", MEMBER_JOIN_REQUEST_FIELDS, errorResponseFields()));
    }
}
