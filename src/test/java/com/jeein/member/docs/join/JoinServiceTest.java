package com.jeein.member.docs.join;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeein.member.dto.request.JoinRequestDTO;
import com.jeein.member.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@ExtendWith(RestDocumentationExtension.class)
public class JoinServiceTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private RestDocumentationResultHandler documentationHandler;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .defaultRequest(get("/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .alwaysDo(document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ))
                .build();
    }

    @Test
    @DisplayName("회원가입 - 이메일 중복 예외")
    void joinMember_emailAlreadyExists() throws Exception {
        // given
        JoinRequestDTO request = JoinRequestDTO.of("email@example.com", "이름", "닉네임", "password");

        // when & then
        mockMvc.perform(post("/api/v1/members/join")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/members/join")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.ALREADY_EXISTING_EMAIL.getCode()))
                .andExpect(jsonPath("$.message").value(containsString(ErrorCode.ALREADY_EXISTING_EMAIL.getMessage())))
                .andDo(document("회원가입 예외 - 이메일 중복",
                        responseFields(
                                fieldWithPath("code").description(ErrorCode.ALREADY_EXISTING_EMAIL.getCode()),
                                fieldWithPath("message").description(ErrorCode.ALREADY_EXISTING_EMAIL.getMessage())
                        )));
    }

    @Test
    @DisplayName("회원가입 - 닉네임 중복 예외")
    void joinMember_nicknameAlreadyExists() throws Exception {
        // given
        JoinRequestDTO request = JoinRequestDTO.of("이름", "닉네임", "email@example.com", "password");

        // when & then
        mockMvc.perform(post("/api/v1/members/join")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        JoinRequestDTO secondRequest = JoinRequestDTO.of("이름", "닉네임", "anotheremail@example.com", "password");

        mockMvc.perform(post("/api/v1/members/join")
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.ALREADY_EXISTING_NICKNAME.getCode()))
                .andExpect(jsonPath("$.message").value(containsString(ErrorCode.ALREADY_EXISTING_NICKNAME.getMessage())))
                .andDo(document("회원가입 예외 - 닉네임 중복",
                        responseFields(
                                fieldWithPath("code").description(ErrorCode.ALREADY_EXISTING_NICKNAME.getCode()),
                                fieldWithPath("message").description(ErrorCode.ALREADY_EXISTING_NICKNAME.getMessage())
                        )));
    }


//    @Test
//    @DisplayName("회원 조회 - 존재하지 않는 ID")
//    void getMember_notFound() throws Exception {
//        mockMvc.perform(get("/api/v1/members/" + UUID.randomUUID()))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.code").value("M_001"))
//                .andExpect(jsonPath("$.message").value("회원 정보를 찾을 수 없습니다."));
//    }
//
//    @Test
//    @DisplayName("닉네임 변경 - 동일한 닉네임으로 요청 시 예외")
//    void updateNickname_unchangedNickname() throws Exception {
//        // 회원 생성
//        JoinRequestDTO request = JoinRequestDTO.of("이름", "닉", "nickname_test@example.com", "password");
//        String memberId = mockMvc.perform(post("/api/v1/members/join")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        String id = objectMapper.readTree(memberId).path("data").path("id").asText();
//
//        UpdateNicknameRequestDTO sameNickname = new UpdateNicknameRequestDTO("닉");
//
//        mockMvc.perform(patch("/api/v1/members/" + id + "/nickname")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(sameNickname)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value("M_003"));
//    }
//
//    @Test
//    @DisplayName("비밀번호 변경 - 기존 비밀번호와 동일한 경우 예외")
//    void updatePassword_unchangedPassword() throws Exception {
//        // 회원 생성
//        JoinRequestDTO request = JoinRequestDTO.of("이름", "닉", "password_test@example.com", "my_password");
//        String memberId = mockMvc.perform(post("/api/v1/members/join")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        String id = objectMapper.readTree(memberId).path("data").path("id").asText();
//
//        UpdatePasswordRequestDTO samePassword = new UpdatePasswordRequestDTO("my_password", "my_password");
//
//        mockMvc.perform(patch("/api/v1/members/" + id + "/password")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(samePassword)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value("M_002"));
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 잘못된 비밀번호")
//    void login_wrongPassword() throws Exception {
//        // 회원 생성
//        JoinRequestDTO request = JoinRequestDTO.of("이름", "닉", "wrongpass@example.com", "right_password");
//        mockMvc.perform(post("/api/v1/members/join")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//
//        // 로그인 시 잘못된 비밀번호
//        LoginRequestDTO loginRequest = new LoginRequestDTO("wrongpass@example.com", "wrong_password");
//
//        mockMvc.perform(post("/api/v1/members/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.code").value("A_004"))
//                .andExpect(jsonPath("$.message").value("비밀번호를 다시 확인해 주세요."));
//    }
}
