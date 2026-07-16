package com.chcorp.homes.policies.service;

import com.chcorp.homes.policies.dto.PolicyScrapDTO;
import com.chcorp.homes.policies.entity.Policy;
import com.chcorp.homes.policies.entity.PolicyScrap;
import com.chcorp.homes.policies.repository.PolicyRepository;
import com.chcorp.homes.policies.repository.PolicyScrapRepository;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.RequestingUserName;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PolicyScrapService {

    private final PolicyScrapRepository scrapRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;

    // 제도 스크랩 등록
    // 이미 스크랩한 제도라면 중복 저장하지 않고 그냥 종료
    public void addScrap(Long userId, Long policyId) {
        if (scrapRepository.existsByUserIdAndPolicyPolicyId(userId, policyId)) {
            return;
        }

        // 현재 로그인한 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 스크랩할 제도 조회
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("제도를 찾을 수 없습니다."));

        // 숨김 처리된 제도는 새로 스크랩할 수 없도록 방지
        if (Boolean.FALSE.equals(policy.getIsVisible())) {
            throw new RuntimeException("비공개 처리된 제도는 스크랩할 수 없습니다.");
        }

        // 스크랩 엔티티 생성 후 저장
        PolicyScrap scrap = PolicyScrap.builder()
                .user(user)
                .policy(policy)
                .build();

        scrapRepository.save(scrap);
    }

    // 제도 스크랩 취소
    // 로그인한 사용자가 특정 제도에 대해 저장한 스크랩을 삭제
    public void removeScrap(Long userId, Long policyId) {
        PolicyScrap scrap = scrapRepository
                .findByUserIdAndPolicyPolicyId(userId, policyId)
                .orElseThrow(() -> new RuntimeException("스크랩을 찾을 수 없습니다."));

        scrapRepository.delete(scrap);
    }

    // 내 제도 스크랩 목록 조회
    // 마이페이지에서 스크랩한 지원제도 리스트를 보여줄 때 사용
    @Transactional
    public List<PolicyScrapDTO> getMyScraps(Long userId) {
        return scrapRepository.findAllByUserId(userId)
                .stream()
                .map(PolicyScrapDTO::new)
                .toList();
    }

    // 내가 스크랩한 제도 id 목록 조회
    // 지원제도 리스트에서 하트 버튼 상태 표시할 때 사용
    @Transactional
    public List<Long> getMyScrapPolicyIds(Long userId) {
        return scrapRepository.findAllByUserId(userId)
                .stream()
                .map(scrap -> scrap.getPolicy().getPolicyId())
                .toList();
    }
}
