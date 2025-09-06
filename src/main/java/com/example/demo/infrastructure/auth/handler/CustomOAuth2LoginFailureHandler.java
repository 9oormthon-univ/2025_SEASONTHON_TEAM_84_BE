package com.example.demo.infrastructure.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomOAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException{
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("소셜 로그인에 실패하였습니다.");
        log.info("소셜 로그인에 실패했습니다. 에러 메시지 : {}", exception.getMessage());

        String requestUrl = request.getRequestURL().toString();
        String query = request.getQueryString();

        String error = request.getParameter("error");
        String errorDescription = request.getParameter("error_description");
        String state = request.getParameter("state");

        String xfProto = request.getHeader("X-Forwarded-Proto");
        String xfHost = request.getHeader("X-Forwarded-Host");
        String xfPort = request.getHeader("X-Forwarded-Port");
        String forwarded = request.getHeader("Forwarded"); // e.g. proto=https;host=...

        // provider 추정 (콜백 경로의 마지막 segment)
        String provider = null;
        String uri = request.getRequestURI(); // /login/oauth2/code/kakao
        int lastSlash = uri != null ? uri.lastIndexOf('/') : -1;
        if (lastSlash >= 0 && lastSlash + 1 < uri.length()) {
            provider = uri.substring(lastSlash + 1);
        }

        log.warn("OAuth2 실패: type={}, msg={}, provider={}, url={}, query={}, error={}, error_description={}, state={}, XFP={}, XFH={}, XFPort={}, Forwarded={}",
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                provider,
                requestUrl,
                query,
                error,
                errorDescription,
                state,
                xfProto,
                xfHost,
                xfPort,
                forwarded
        );
    }
}