package com.chcorp.homes.announcement.controller;

import com.chcorp.homes.announcement.dto.AnnouncementListDTO;
import com.chcorp.homes.announcement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public List<AnnouncementListDTO> getAnnouncements(
            @RequestParam(required = false) String recruitmentType
    ) {
        return announcementService.getAnnouncements(recruitmentType);
    }
}