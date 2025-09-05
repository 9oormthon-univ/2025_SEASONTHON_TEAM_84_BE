package com.example.demo.domain.store.exception;

import com.example.demo.infrastructure.exception.payload.code.BaseCode;
import com.example.demo.infrastructure.exception.payload.code.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum StoreErrorStatus implements BaseCode {

    // Store Error (4300 ~ 4349)
    STORE_NOT_FOUND(NOT_FOUND, 4300, "찾을 수 없는 업소 정보입니다."),
    STORE_ALREADY_EXISTS(BAD_REQUEST, 4301, "이미 존재하는 업소입니다."),
    STORE_INACTIVE(BAD_REQUEST, 4302, "비활성화된 업소입니다."),
    INVALID_COORDINATES(BAD_REQUEST, 4303, "유효하지 않은 좌표입니다."),
    INVALID_LATITUDE(BAD_REQUEST, 4304, "유효하지 않은 위도값입니다. (-90 ~ 90)"),
    INVALID_LONGITUDE(BAD_REQUEST, 4305, "유효하지 않은 경도값입니다. (-180 ~ 180)"),
    INVALID_RADIUS(BAD_REQUEST, 4306, "유효하지 않은 반경값입니다."),
    RADIUS_TOO_LARGE(BAD_REQUEST, 4307, "검색 반경이 너무 큽니다. (최대 100km)"),
    INVALID_BUSINESS_TYPE(BAD_REQUEST, 4308, "유효하지 않은 업종입니다."),
    INVALID_MENU_PRICE(BAD_REQUEST, 4309, "유효하지 않은 메뉴 가격입니다."),;

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
