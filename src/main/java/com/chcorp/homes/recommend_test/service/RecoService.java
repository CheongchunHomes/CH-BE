package com.chcorp.homes.recommend_test.service;

import com.chcorp.homes.diagnosis.entity.DiagnosisResult;
import com.chcorp.homes.diagnosis.repository.DiagnosisRepository;
import com.chcorp.homes.recommend_test.entity.Recoentity;
import com.chcorp.homes.recommend_test.repository.Recorepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecoService {

    private final Recorepository recorepository;
    private final UserProfileRepository userProfileRepository;
    private final DiagnosisRepository diagnosisRepository;

    //유저 프로필 조회
    public UserProfile getUserProfile(Long userId) {
        return userProfileRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("유저프로필없음"));
    }

    //진단 결과 조회
    public DiagnosisResult getDiagnosis(Long userId) {
        return diagnosisRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("진단결과없음"));
    }

    //유저 조건 기반 정책 추천 DB user_profiles에서 나이/소득/지역 꺼낸 후 recoentity 필터링
    public List<Recoentity> getRecommendPolicies(Long userId) {
        UserProfile user = getUserProfile(userId);
        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        long annualIncomeInManwon = user.getAnnualIncome() / 10000; //원 > 만원단위로 소득변환
        String region = user.getCurrentResidence();
        return  recorepository.findByActiveTrue().stream().filter(p -> age >= p.getMinAge() && age <= p.getMaxAge())
                .filter(p -> annualIncomeInManwon <= p.getMaxIncome()).filter(p -> p.getRegion().equals("전국") || p.getRegion().equals(region)).collect(Collectors.toList());
    }

    // 프로필 + 진단 + 추천 정책 한번에 반환
    public Map<String, Object> getSummary(Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("profile", getUserProfile(userId));
        result.put("diagnosis", getDiagnosis(userId));
        result.put("policies", getRecommendPolicies(userId));
        return result;
    }
    /** 전체 조회 */
    public List<Recoentity> getAll() {
        return recorepository.findAll();
    }

    /** 파라미터 직접 넘겨서 필터링 (기존 방식) */
    public List<Recoentity> recommend(int age, int income, String region) {
        return recorepository.findByActiveTrue().stream()
                .filter(p -> age >= p.getMinAge() && age <= p.getMaxAge())
                .filter(p -> income <= p.getMaxIncome())
                .filter(p -> p.getRegion().equals("전국") || p.getRegion().equals(region))
                .collect(Collectors.toList());
    }

    /** 단건 조회 */
    public Recoentity getById(Long id) {
        return recorepository.findById(id).orElseThrow();
    }

    /** 추가 */
    public Recoentity create(Recoentity entity) {
        return recorepository.save(entity);
    }

    /** 수정 */
    public Recoentity update(Long id, Recoentity updated) {
        Recoentity entity = getById(id);
        entity.setName(updated.getName());
        entity.setCategory(updated.getCategory());
        entity.setDescription(updated.getDescription());
        entity.setActive(updated.isActive());
        return recorepository.save(entity);
    }

    /** 삭제 */
    public void delete(Long id) {
        recorepository.deleteById(id);
    }
}



