package com.example.demo.presentation.auth.controller;

import com.example.demo.application.auth.SignInUseCase;
import com.example.demo.application.auth.SignUpUseCase;
import com.example.demo.domain.member.entity.Member;
import com.example.demo.infrastructure.exception.payload.dto.ApiResponseDto;
import com.example.demo.infrastructure.security.aop.CurrentMember;
import com.example.demo.presentation.auth.dto.AuthRequest;
import com.example.demo.presentation.auth.dto.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "01. Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignUpUseCase signUpUseCase;
    private final SignInUseCase signInUseCase;

    @Operation(
            summary = "회원가입",
            description = "일반 회원가입을 진행합니다. 아이디와 닉네임은 중복될 수 없습니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (아이디/닉네임 중복 등)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "유효성 검사 실패")
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseDto<AuthResponse.SignUpResponse> signUp(
            @Valid @RequestBody AuthRequest.SignUpRequest request
    ) {
        AuthResponse.SignUpResponse response = signUpUseCase.execute(request);
        return ApiResponseDto.onSuccess(response);
    }

    @Operation(
            summary = "로그인",
            description = "일반 로그인을 진행합니다. 소셜 로그인으로 가입된 계정은 로그인할 수 없습니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (아이디/비밀번호 오류)")
    })
    @PostMapping("/signin")
    public ApiResponseDto<AuthResponse.SignInResponse> signIn(
            @Valid @RequestBody AuthRequest.SignInRequest request
    ) {
        AuthResponse.SignInResponse response = signInUseCase.execute(request);
        return ApiResponseDto.onSuccess(response);
    }

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    public ApiResponseDto<AuthResponse.MemberInfo> getMyInfo(
            @Parameter(hidden = true) @CurrentMember Member member
    ) {
        AuthResponse.MemberInfo memberInfo = AuthResponse.MemberInfo.from(member);
        return ApiResponseDto.onSuccess(memberInfo);
    }

}
