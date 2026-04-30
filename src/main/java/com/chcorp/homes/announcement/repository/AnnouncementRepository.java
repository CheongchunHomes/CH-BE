package com.chcorp.homes.announcement.repository;

import com.chcorp.homes.announcement.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByRecruitmentTypeAndIsVisibleTrueOrderByApplyStartDateAsc(String recruitmentType);

    List<Announcement> findBySourceTypeAndIsVisibleTrueOrderByApplyStartDateAsc(String sourceType);

    List<Announcement> findByIsVisibleTrueOrderByApplyStartDateAsc();
}