package com.example.demo.presentation.auth.dto;

import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.member.entity.Role;
import com.example.demo.infrastructure.security.dto.JwtToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class AuthResponse {

    @Getter
    @Builder
    @Schema(description = "회원가입 응답")
    public static class SignUpResponse {
        
        @Schema(description = "사용자 ID")
        private Long memberId;
        
        @Schema(description = "사용자 아이디")
        private String username;
        
        @Schema(description = "사용자 닉네임")
        private String nickname;
        
        @Schema(description = "사용자 역할")
        private Role role;

        public static SignUpResponse from(Member member) {
            return SignUpResponse.builder()
                    .memberId(member.getId())
                    .username(member.getUsername())
                    .nickname(member.getNickname())
                    .role(member.getRole())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "로그인 응답")
    public static class SignInResponse {
        
        @Schema(description = "JWT 토큰 타입", example = "Bearer")
        private String grantType;
        
        @Schema(description = "액세스 토큰")
        private String accessToken;
        
        @Schema(description = "사용자 정보")
        private MemberInfo memberInfo;

        public static SignInResponse from(JwtToken jwtToken, Member member) {
            return SignInResponse.builder()
                    .grantType(jwtToken.getGrantType())
                    .accessToken(jwtToken.getAccessToken())
                    .memberInfo(MemberInfo.from(member))
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "사용자 정보")
    public static class MemberInfo {
        
        @Schema(description = "사용자 ID")
        private Long memberId;
        
        @Schema(description = "사용자 아이디")
        private String username;
        
        @Schema(description = "사용자 닉네임")
        private String nickname;

        @Schema(description = "사용자 역할")
        private Role role;


        public static MemberInfo from(Member member) {
            return MemberInfo.builder()
                    .memberId(member.getId())
                    .username(member.getUsername())
                    .nickname(member.getNickname())
                    .role(member.getRole())
                    .build();
        }
    }

    @Getter
    @Builder
    @Schema(description = "토큰 갱신 응답")
    public static class RefreshTokenResponse {
        
        @Schema(description = "JWT 토큰 타입", example = "Bearer")
        private String grantType;
        
        @Schema(description = "새로운 액세스 토큰")
        private String accessToken;
        
        @Schema(description = "새로운 리프레시 토큰")
        private String refreshToken;

        public static RefreshTokenResponse from(JwtToken jwtToken) {
            return RefreshTokenResponse.builder()
                    .grantType(jwtToken.getGrantType())
                    .accessToken(jwtToken.getAccessToken())
                    .refreshToken(jwtToken.getRefreshToken())
                    .build();
        }
    }
}
