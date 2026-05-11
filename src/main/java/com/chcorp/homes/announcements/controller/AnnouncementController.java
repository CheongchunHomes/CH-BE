package com.chcorp.homes.announcements.controller;


import com.chcorp.homes.announcements.dto.AnnouncementListDTO;
import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.service.AnnouncementService;
import com.chcorp.homes.announcements.service.ApplyhomeAnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")   // React 앱이 다른 포트에서 실행될 때 CORS 허용
@RequiredArgsConstructor    // AnnouncementService 주입
@RequestMapping("/announcements")   // Postman 주소 앞 부분 일치
@RestController     // API 데이터를 반환하기 위해
// 목록형 API 호출 Controller
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final ApplyhomeAnnouncementService applyhomeService;

    @PostMapping("/fetch")
    public ResponseEntity<?> fetchAnnouncements(@RequestParam("brtcCode") String brtcCode) {
        // 서비스에서 fetchAll 메서드 호출
        announcementService.fetchAll(brtcCode);
        return ResponseEntity.ok("지역코드 [" + brtcCode + "] 데이터 수집 완료");
    }

    // 모든 지역 데이터를 한 번에 수집하고 싶을 때 사용
    @PostMapping("/fetch/all")
    public ResponseEntity<String> fetchAllRegions() {
        announcementService.fetchAllRegions();
        return ResponseEntity.ok("전체 지역 데이터 수집 완료");
    }

    // 청약홈 api 추가
    @PostMapping("/fetch/applyhome")
    public ResponseEntity<String> fetchApplyhome() {
        applyhomeService.fetchApplyhome();
        return ResponseEntity.ok("청약홈 데이터 수집 완료");
    }

    // DTO에 있는 정보만 받고 싶을때
    @GetMapping
    public ResponseEntity<Page<AnnouncementListDTO>> getList(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "false") boolean deadlineSoon,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        //1. 서비스에서 DB 데이터를 Page 형태로 가져옴
        Page<Announcement> announcements = announcementService.getList(region, status, keyword, deadlineSoon, page, size);

        //2. 가져온 데이터 (announcements)를 DTO로 변환
        Page<AnnouncementListDTO> dto = announcements.map(AnnouncementListDTO::new);

        //3. DTO 형태로 반환
        return ResponseEntity.ok(dto);
    }

    // api 단건 조회 (공고 상세페이지 용)
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementListDTO> getOne(@PathVariable Long id) {
        Announcement announcement = announcementService.getOne(id);
        return ResponseEntity.ok(new AnnouncementListDTO(announcement));
    }
}