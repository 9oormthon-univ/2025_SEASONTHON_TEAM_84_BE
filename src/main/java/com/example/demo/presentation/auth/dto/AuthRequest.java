package com.example.demo.presentation.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequest {

    @Getter
    @NoArgsConstructor
    @Schema(description = "회원가입 요청")
    public static class SignUpRequest {
        
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다.")
        @Schema(description = "사용자 아이디", example = "testuser123")
        private String username;
        
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다.")
        @Schema(description = "비밀번호", example = "password123!")
        private String password;
        
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
        @Schema(description = "사용자 닉네임", example = "테스트유저")
        private String nickname;
        
//        @Schema(description = "이메일 (선택사항)", example = "test@example.com")
//        private String email;

        public SignUpRequest(String username, String password, String nickname) {
            this.username = username;
            this.password = password;
            this.nickname = nickname;
//            this.email = email;
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "로그인 요청")
    public static class SignInRequest {
        
        @NotBlank(message = "아이디는 필수입니다.")
        @Schema(description = "사용자 아이디", example = "testuser123")
        private String username;
        
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Schema(description = "비밀번호", example = "password123!")
        private String password;

        public SignInRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
