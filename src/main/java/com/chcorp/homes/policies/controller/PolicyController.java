package com.chcorp.homes.policies.controller;

import com.chcorp.homes.policies.dto.PolicyDetailDTO;
import com.chcorp.homes.policies.dto.PolicyListDTO;
import com.chcorp.homes.policies.entity.Policy;
import com.chcorp.homes.policies.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/policies")
@RestController
public class PolicyController {

    private final PolicyService policyService;

    // 청년정책 api 데이터 수집
    @PostMapping("/fetch/youth")
    public ResponseEntity<String> fetchYouthPolicies() {
        policyService.fetchYouthPolicies();
        return ResponseEntity.ok("청년정책 데이터 수집 완료");
    }

    // 행안부 공공서비스 정책 api 데이터 수집
    @PostMapping("/fetch/public-service")
    public ResponseEntity<String> fetchPublicServices() {
        policyService.fetchPublicServices();
        return ResponseEntity.ok("행안부 공공서비스 정책 데이터 수집 완료");
    }

    // DTO에 있는 정보만 받고 싶을때
    @GetMapping
    public ResponseEntity<Page<PolicyListDTO>> getList(
            @RequestParam(required = false) String mainCategory,
            @RequestParam(required = false) String subCategory,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 1. 서비스에서 DB 데이터를 page 형태로 가져옴
        Page<Policy> policies = policyService.getList(mainCategory, subCategory, keyword, page, size);

        // 2. 가져온 데이터를 DTO로 반환
        Page<PolicyListDTO> dto = policies.map(PolicyListDTO::new);

        // 3. DTO 형태로 반환
        return ResponseEntity.ok(dto);
    }

    // API 단건 조회 지원제도 상세페이지용
    @GetMapping("/{id}")
    public ResponseEntity<PolicyDetailDTO> getOne(@PathVariable Long id) {
        Policy policy = policyService.getOne(id);
        return ResponseEntity.ok(new PolicyDetailDTO(policy));
    }
}
