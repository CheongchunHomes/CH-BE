package com.chcorp.homes.recommend.service;

import com.chcorp.homes.recommend.entity.Recoentity;
import com.chcorp.homes.recommend.repository.Recorepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecoService {

    private final Recorepository recorepository;

    //전체 조회
    public List <Recoentity> getAll() {
        return recorepository.findAll();
    }
    /** 파라미터 직접 넘겨서 필터링 */
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

//        오류방지
    public Map<String, Object> getSummary(Long userId) {
            Map<String, Object> result = new HashMap<>();

        // TODO: Step 1 - user_diagnosis_results 테이블 연결 후 교체
        // UserDiagnosisResult diagnosis = diagnosisRepository.findByUserId(userId)
        // RecommendCondition cond = RecommendCondition.builder()
        //         .homelessStatus(diagnosis.getHomelessStatus())
        //         .ageStatus(diagnosis.getAgeStatus())
        //         .incomeStatus(diagnosis.getIncomeStatus())
        //         .subscriptionStatus(diagnosis.getSubscriptionStatus())
        //         .build();

        // TODO: Step 2 - announcement 테이블 연결 후 교체
        // List<Announcement> matched = announcementFilterService.filterAnnouncements(cond);
        // result.put("policies", matched);
            result.put("profile", null);
            result.put("diagnosis", null);
            //밑 부분 나중에 리스트 메서드 받으면 여기 넣어서 뿌리면됨
            result.put("policies", recorepository.findByActiveTrue());
            return result;

    }
}



