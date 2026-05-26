package com.chcorp.homes.simulator.exception;

/**
 * 다른 유저의 플랜에 접근하는 상태.
 * AssetPlanController에서 403으로 변환.
 */
public class PlanAccessDeniedException extends RuntimeException {

    public PlanAccessDeniedException() {
        super("Access denied");
    }
}