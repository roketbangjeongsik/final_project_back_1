package com.finalproject.backend.exception;

/**
 * 인증 실패 시 사용되는 커스텀 예외 클래스입니다.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
