package com.chcorp.homes.announcements.service;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementCoordinateService {

    private final AnnouncementRepository announcementRepository;
    private final KakaoGeocodingService kakaoGeocodingService;

    // 좌표가 없는 공고를 최대 100개씩 조회해서 주소 기반 좌표를 채움
    @Transactional
    public CoordinateUpdateResult updateMissingCoordinates() {
        List<Announcement> announcements =
                announcementRepository.findGeocodeTargets(PageRequest.of(0,1000));

        int successCount = 0;
        int failCount = 0;

        for (Announcement announcement : announcements) {
            String address = announcement.getAddress();

            if (address == null || address.isBlank()) {
                failCount++;
                continue;
            }

            KakaoGeocodingService.Coordinate coordinate =
                    kakaoGeocodingService.getCoordinateByAddress(address);

            if (coordinate == null) {
                failCount++;
                continue;
            }

            announcement.updateCoordinates(
                    coordinate.getLatitude(),
                    coordinate.getLongitude()
            );

            successCount++;

            log.info(
                    "[공고 좌표 업데이트] announcementId={}, address={}, latitude={}, longitude={}",
                    announcement.getAnnouncementId(),
                    address,
                    coordinate.getLatitude(),
                    coordinate.getLongitude()
            );
        }

        log.info(
                "[공고 좌표 업데이트 완료] 대상 {}건, 성공 {}건, 실패 {}건",
                announcements.size(),
                successCount,
                failCount
        );

        return new CoordinateUpdateResult(
                announcements.size(),
                successCount,
                failCount
        );
    }

    @Getter
    @AllArgsConstructor
    public static class CoordinateUpdateResult {
        private int targetCount;
        private int successCount;
        private int failCount;
    }
}