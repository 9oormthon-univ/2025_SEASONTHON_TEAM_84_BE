package com.example.demo.application.auth;

import com.example.demo.domain.member.adaptor.MemberAdaptor;
import com.example.demo.domain.member.entity.Member;
import com.example.demo.domain.member.entity.Role;
import com.example.demo.infrastructure.annotation.usecase.UseCase;
import com.example.demo.infrastructure.exception.object.general.GeneralException;
import com.example.demo.infrastructure.exception.payload.code.ErrorStatus;
import com.example.demo.presentation.auth.dto.AuthRequest;
import com.example.demo.presentation.auth.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class SignUpUseCase {

    private final MemberAdaptor memberAdaptor;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse.SignUpResponse execute(AuthRequest.SignUpRequest request) {
        validateSignUpRequest(request);
        
        Member member = createMember(request);
        Member savedMember = memberAdaptor.save(member);
        
        return AuthResponse.SignUpResponse.from(savedMember);
    }

    private void validateSignUpRequest(AuthRequest.SignUpRequest request) {
        // 아이디 중복 체크
        if (memberAdaptor.existsByUsername(request.getUsername())) {
            throw new GeneralException(ErrorStatus.MEMBER_DUPLICATE_USERNAME);
        }
        
        // 닉네임 중복 체크
        if (memberAdaptor.existsByNickname(request.getNickname())) {
            throw new GeneralException(ErrorStatus.MEMBER_DUPLICATE_NICKNAME);
        }
    }

    private Member createMember(AuthRequest.SignUpRequest request) {
        return Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .email(request.getEmail())
                .role(Role.USER)
                .point(0)
                .build();
    }
}
