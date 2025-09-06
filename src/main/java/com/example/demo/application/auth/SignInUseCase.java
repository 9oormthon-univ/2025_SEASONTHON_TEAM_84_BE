package com.example.demo.application.auth;

import com.example.demo.domain.member.adaptor.MemberAdaptor;
import com.example.demo.domain.member.entity.Member;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import com.example.demo.infrastructure.exception.object.general.GeneralException;
import com.example.demo.infrastructure.exception.payload.code.ErrorStatus;
import com.example.demo.infrastructure.security.dto.JwtToken;
import com.example.demo.infrastructure.security.service.TokenService;
import com.example.demo.infrastructure.security.vo.CustomUserDetails;
import com.example.demo.presentation.auth.dto.AuthRequest;
import com.example.demo.presentation.auth.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignInUseCase {

    private final MemberAdaptor memberAdaptor;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthResponse.SignInResponse execute(AuthRequest.SignInRequest request) {
        Member member = validateAndGetMember(request);
        
        Authentication authentication = createAuthentication(member);
        JwtToken jwtToken = tokenService.generateToken(authentication);
        
        return AuthResponse.SignInResponse.from(jwtToken, member);
    }

    private Member validateAndGetMember(AuthRequest.SignInRequest request) {
        Member member;
        try {
            member = memberAdaptor.queryByUsername(request.getUsername());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.AUTH_INVALID_CREDENTIALS);
        }
        
        // 소셜 로그인 사용자인지 확인 (password가 null인 경우)
        if (member.getPassword() == null) {
            throw new GeneralException(ErrorStatus.AUTH_SOCIAL_LOGIN_ONLY);
        }
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new GeneralException(ErrorStatus.AUTH_INVALID_CREDENTIALS);
        }
        
        return member;
    }

    private Authentication createAuthentication(Member member) {
        CustomUserDetails userDetails = new CustomUserDetails(member);
        return new UsernamePasswordAuthenticationToken(
                userDetails, 
                null, 
                userDetails.getAuthorities()
        );
    }
}
