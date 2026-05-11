package com.chcorp.homes.auth.exception;

/**
 * Refresh token은 유효하지만 수명의 70%가 경과해 재인증이 필요한 상태.
 * AuthController에서 401 + code=REAUTH_REQUIRED 로 변환한다.
 */
public class ReauthRequiredException extends RuntimeException {

    public ReauthRequiredException() {
        super("Reauth required");
    }
}
