package com.chcorp.homes.recommend_test.controller;

import com.chcorp.homes.diagnosis.entity.DiagnosisResult;
import com.chcorp.homes.recommend_test.entity.Recoentity;
import com.chcorp.homes.recommend_test.service.RecoService;
import com.chcorp.homes.users.entity.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class RecController {

    private final RecoService recoService;

    //가짜유저
    private static final Long FAKE_USER_ID = 1L;

    // 유저 프로필 조회 GET/api/recommend/profile
    @GetMapping("/profile")
    public UserProfile getProfile() {
        return recoService.getUserProfile(FAKE_USER_ID);
    }

    // 진단 결과 조회 (점수/등급) GET/api/recommend/diagnosis
    @GetMapping("/diagnosis")
    public DiagnosisResult getDiagnosis() {
        return recoService.getDiagnosis(FAKE_USER_ID);
    }

    // 조건 기반 정책 추천 GET/api/recommend/policies
    @GetMapping("/policies")
    public List<Recoentity>getRecommendedPolicies(){
        return recoService.getRecommendPolicies(FAKE_USER_ID);
    }

    // 전체 통합 결과 GET/api/recommend/summary
    @GetMapping("/summary")
    public Map<String, Object> getSummary(){
        return recoService.getSummary(FAKE_USER_ID);
    }

    //전체조회
    @GetMapping
    public List<Recoentity> getAll() {
        return recoService.getAll();
    }
    //파라미터 기반 추천
    @GetMapping("/recommend")
    public List<Recoentity> recommend(
            @RequestParam int age,
            @RequestParam int income,
            @RequestParam String region
    ) {
        return recoService.recommend(age, income, region);
    }

    //단건조회
    @GetMapping("/{id}")
    public Recoentity getById(@PathVariable Long id) {
        return recoService.getById(id);
    }

    //정책추가
    @PostMapping
    public Recoentity create(@RequestBody Recoentity recoentity) {
        return recoService.create(recoentity);
    }

    //정책수정
    @PutMapping("/{id}")
    public Recoentity update(@PathVariable Long id, @RequestBody Recoentity recoentity) {
        return recoService.update(id, recoentity);
    }
    //정책삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        recoService.delete(id);
    }
}


