package com.chcorp.homes.auth.exception;

/**
 * 인증 대상 사용자가 비활성화 상태인 경우 던진다.
 * AuthController에서 401 + code=USER_DISABLED 로 변환한다.
 */
public class DisabledUserException extends RuntimeException {

    public DisabledUserException() {
        super("User disabled");
    }
}
