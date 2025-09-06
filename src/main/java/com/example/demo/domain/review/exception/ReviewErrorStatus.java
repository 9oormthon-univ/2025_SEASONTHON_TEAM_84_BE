package com.example.demo.domain.review.exception;

import com.example.demo.infrastructure.exception.payload.code.BaseCode;
import com.example.demo.infrastructure.exception.payload.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ReviewErrorStatus implements BaseCode {

    REVIEW_NOT_FOUND(NOT_FOUND, 4400, "찾을 수 없는 리뷰 정보입니다."),
    INVALID_RATING_VALUE(BAD_REQUEST, 4401, "유효하지 않은 별점값입니다. (1~5)");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    @Override
    public Reason getReason() {
        return Reason.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public Reason getReasonHttpStatus() {
        return Reason.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
