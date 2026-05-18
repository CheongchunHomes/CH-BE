package com.chcorp.homes.policies.controller;

import com.chcorp.homes.policies.dto.PolicyScrapDTO;
import com.chcorp.homes.policies.service.PolicyScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/policy-scraps")
public class PolicyScrapController {

    private final PolicyScrapService policyScrapService;

    // 제도 스크랩 등록
    // 로그인한 사용자가 특정 제도를 스크랩할 때 사용
    @PostMapping("/{policyId}")
    public ResponseEntity<Void> addScrap(
            @PathVariable Long policyId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        policyScrapService.addScrap(userId, policyId);
        return ResponseEntity.ok().build();
    }

    // 제도 스크랩 취소
    // 로그인한 사용자가 이미 스크랩한 지원제도를 해제할 때 사용
    @DeleteMapping("/{policyId}")
    public ResponseEntity<Void> removeScrap(
            @PathVariable Long policyId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        policyScrapService.removeScrap(userId, policyId);
        return ResponseEntity.ok().build();
    }

    // 내 제도 스크랩 목록 조회
    // 마이페이지에서 스크랩한 지원제도 리스트를 보여줄 때 사용
    @GetMapping("/me")
    public ResponseEntity<List<PolicyScrapDTO>> getMyScraps(
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(policyScrapService.getMyScraps(userId));
    }

    // 내가 스크랩한 제도 id 목록 조회
    // 지원제도 리스트/상세페이지에서 하트 상태를 표시할 때 사용
    @GetMapping("/me/ids")
    public ResponseEntity<List<Long>> getMyScrapPolicyIds(
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(policyScrapService.getMyScrapPolicyIds(userId));
    }

    // JWT 인증 정보에서 로그인한 사용자 id 추출
    // JwtTokenProvider에서 principal에 userId를 문자열로 넣어두었기 때문에 getName()으로 가져옴
    private Long getUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        return Long.valueOf(authentication.getName());
    }
}
