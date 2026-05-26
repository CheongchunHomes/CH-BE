package com.chcorp.homes.simulator.exception;

/**
 * 플랜을 찾을 수 없는 상태.
 * AssetPlanController에서 404로 변환.
 */
public class PlanNotFoundException extends RuntimeException {

    public PlanNotFoundException() {
        super("Plan not found");
    }
}