package com.jeein.member.docs.login;

import static com.jeein.member.docs.RestDocsUtil.doc;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeein.member.docs.ApiPath;
import com.jeein.member.docs.DocumentIdentifier;
import com.jeein.member.dto.request.LoginRequestDTO;
import com.jeein.member.exception.ErrorCode;
import com.jeein.member.service.MemberService;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
@Import(LoginRequestValidationTest.TestMockConfig.class)
@DisplayName("로그인 요청 유효성 예외 테스트")
public class LoginRequestValidationTest {

    @Autowired private WebApplicationContext context;

    @Autowired private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(context)
                        .apply(documentationConfiguration(restDocumentation))
                        .defaultRequest(
                                get("/").accept(MediaType.APPLICATION_JSON)
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

    private static Map<String, String> createInvalidMapWithField(
            Map<String, String> base, String field, String value) {
        Map<String, String> copy = new HashMap<>(base);
        copy.put(field, value);
        return copy;
    }

    private static Stream<Arguments> provideInvalidLoginRequests() {
        LoginRequestDTO validRequestDto = LoginRequestDTO.of("test@example.com", "password123");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> validMap =
                mapper.convertValue(validRequestDto, new TypeReference<>() {});

        return Stream.of(emailValidationCases(validMap), passwordValidationCases(validMap))
                .flatMap(Function.identity());
    }

    private static Stream<Arguments> emailValidationCases(Map<String, String> base) {
        return Stream.of(
                Arguments.of(
                        "이메일 패턴 유효하지 않음",
                        createInvalidMapWithField(base, "email", "invalid-email"),
                        "email",
                        DocumentIdentifier.LOGIN_VALIDATION_EMAIL + "/pattern"),
                Arguments.of(
                        "이메일 빈 문자열",
                        createInvalidMapWithField(base, "email", ""),
                        "email",
                        DocumentIdentifier.LOGIN_VALIDATION_EMAIL + "/blank"),
                Arguments.of(
                        "이메일 null",
                        createInvalidMapWithField(base, "email", null),
                        "email",
                        DocumentIdentifier.LOGIN_VALIDATION_EMAIL + "/null"),
                Arguments.of(
                        "이메일 50자 초과",
                        createInvalidMapWithField(base, "email", "a".repeat(40) + "@example.com"),
                        "email",
                        DocumentIdentifier.LOGIN_VALIDATION_EMAIL + "/size"));
    }

    private static Stream<Arguments> passwordValidationCases(Map<String, String> base) {
        return Stream.of(
                Arguments.of(
                        "비밀번호 빈 문자열",
                        createInvalidMapWithField(base, "password", ""),
                        "password",
                        DocumentIdentifier.LOGIN_VALIDATION_PASSWORD + "/blank"),
                Arguments.of(
                        "비밀번호 null",
                        createInvalidMapWithField(base, "password", null),
                        "password",
                        DocumentIdentifier.LOGIN_VALIDATION_PASSWORD + "/null"),
                Arguments.of(
                        "비밀번호 20자 초과",
                        createInvalidMapWithField(base, "password", "a".repeat(21)),
                        "password",
                        DocumentIdentifier.LOGIN_VALIDATION_PASSWORD + "/size"));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideInvalidLoginRequests")
    @DisplayName("로그인 요청 유효성 검사 실패 시 400 에러와 원인 필드를 반환한다.")
    void login_withInvalidField_shouldReturnBadRequest(
            String testName,
            Map<String, String> requestMap,
            String expectedField,
            String docDirectory)
            throws Exception {
        String requestJson = objectMapper.writeValueAsString(requestMap);

        mockMvc.perform(post(ApiPath.MEMBER_LOGIN).content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_REQUEST_VALUE.getCode()))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        containsString(
                                                ErrorCode.INVALID_REQUEST_VALUE.getMessage())))
                .andExpect(jsonPath("$.errors[0].field").value(expectedField))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andDo(doc(docDirectory));
    }
}
