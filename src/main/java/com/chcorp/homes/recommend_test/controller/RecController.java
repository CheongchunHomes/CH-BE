package com.chcorp.homes.recommend_test.controller;

import com.chcorp.homes.recommend_test.entity.Recoentity;
import com.chcorp.homes.recommend_test.service.RecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecController {

    private final RecoService recoService;

    /** 전체 조회 */
    @GetMapping
    public List<Recoentity> getAll() {
        return recoService.getAll();
    }

    /** 파라미터 기반 추천 */
    @GetMapping("/filter")
    public List<Recoentity> recommend(
            @RequestParam int age,
            @RequestParam int income,
            @RequestParam String region
    ) {
        return recoService.recommend(age, income, region);
    }

    /** 단건 조회 */
    @GetMapping("/{id}")
    public Recoentity getById(@PathVariable Long id) {
        return recoService.getById(id);
    }

    /** 정책 추가 */
    @PostMapping
    public Recoentity create(@RequestBody Recoentity recoentity) {
        return recoService.create(recoentity);
    }

    /** 정책 수정 */
    @PutMapping("/{id}")
    public Recoentity update(@PathVariable Long id, @RequestBody Recoentity recoentity) {
        return recoService.update(id, recoentity);
    }

    /** 정책 삭제 */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        recoService.delete(id);
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return recoService.getSummary(1L);
    }
}