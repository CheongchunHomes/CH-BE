package com.chcorp.homes.announcement.controller;

import com.chcorp.homes.announcement.dto.AnnouncementListResponse;
import com.chcorp.homes.announcement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:3001"
})
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public List<AnnouncementListResponse> getAnnouncements(
            @RequestParam(required = false) String recruitmentType
    ) {
        return announcementService.getAnnouncements(recruitmentType);
    }
}