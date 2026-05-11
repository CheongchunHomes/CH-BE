package com.chcorp.homes.auth.exception;

/**
 * Refresh token이 만료된 상태.
 * AuthController에서 401 + code=REFRESH_EXPIRED 로 변환한다.
 */
public class RefreshExpiredException extends RuntimeException {

    public RefreshExpiredException() {
        super("Refresh token expired");
    }
}
