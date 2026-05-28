package com.chcorp.homes.admin.controller;

import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import com.chcorp.homes.community.repository.CommunityPostRepository;
import com.chcorp.homes.notice.repository.NoticeRepository;
import com.chcorp.homes.policies.repository.PolicyRepository;
import com.chcorp.homes.properties.repository.PropertyRepository;
import com.chcorp.homes.subscription.repository.SubscriptionRepository;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.entity.UserRole;
import com.chcorp.homes.users.entity.UserStatus;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.chcorp.homes.community.repository.CommunityPostRepository;
import com.chcorp.homes.notice.repository.NoticeRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("MM.dd HH:mm");

    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PropertyRepository propertyRepository;
    private final PolicyRepository policyRepository;
    private final CommunityPostRepository communityPostRepository;
    private final NoticeRepository noticeRepository;

    @GetMapping
    public String admin(@RequestParam(value = "section", defaultValue = "overview") String section, Model model) {
        String currentSection = normalizeSection(section);
        SectionView sectionView = buildSectionView(currentSection);

        model.addAttribute("section", currentSection);
        model.addAttribute("isOverview", "overview".equals(currentSection));
        model.addAttribute("menuItems", buildMenuItems(currentSection));
        model.addAttribute("sectionTitle", sectionView.title());
        model.addAttribute("sectionDescription", sectionView.description());
        model.addAttribute("stats", buildStats());
        model.addAttribute("overviewActions", buildOverviewActions());
        model.addAttribute("recentLogs", buildRecentLogs());
        model.addAttribute("tableHeaders", sectionView.tableView().headers());
        model.addAttribute("tableRows", sectionView.tableView().rows());
        model.addAttribute("sectionBadge", sectionLabel(currentSection));

        return "admin";
    }

    private SectionView buildSectionView(String section) {
        return switch (section) {
            case "users" -> new SectionView(
                    "유저 리스트",
                    "가입 유저의 계정, 권한, 상태를 확인합니다.",
                    buildUserTable()
            );
            case "subscription" -> new SectionView(
                    "청약 리스트",
                    "청약 공고와 모집 기간을 확인합니다.",
                    buildSubscriptionTable()
            );
            case "loan" -> new SectionView(
                    "대출 리스트",
                    "대출 계약, 서명, PDF 저장 상태를 확인합니다.",
                    buildLoanTable()
            );
            case "announcement" -> new SectionView(
                    "공고 리스트",
                    "노출 중인 공고의 지역, 유형, 상태를 관리합니다.",
                    buildAnnouncementTable()
            );
            case "policy" -> new SectionView(
                    "지원제도 리스트",
                    "노출 중인 지원제도의 유형과 상태를 관리합니다.",
                    buildPolicyTable()
            );
            case "notice" -> new SectionView(
                    "공지사항 관리",
                    "서비스 공지사항을 등록하고 확인합니다.",
                    buildNoticeTable()
            );
            case "asset" -> new SectionView(
                    "자산 리스트",
                    "이미지, PDF, 첨부 자산의 저장 상태를 확인합니다.",
                    buildStaticTable(
                            List.of("파일명", "분류", "상태", "메모"),
                            List.of(
                                    row("main-banner.png", "이미지", "사용 중", "메인 배너"),
                                    row("contract-sample.pdf", "문서", "보관 중", "계약서 샘플"),
                                    row("signature.png", "이미지", "보관 중", "전자서명 예시")
                            )
                    )
            );
            case "community" -> new SectionView(
                    "커뮤니티 리스트",
                    "등록된 커뮤니티 게시글을 확인합니다.",
                    buildCommunityTable()
            );
            case "simulation" -> new SectionView(
                    "시뮬레이션 리스트",
                    "조건 진단과 대출 계산 요청을 확인합니다.",
                    buildStaticTable(
                            List.of("이름", "유형", "상태", "업데이트"),
                            List.of(
                                    row("조건 진단", "진단", "완료", "2분 전"),
                                    row("대출 계산", "계산", "완료", "15분 전"),
                                    row("계약서 생성", "문서", "완료", "31분 전")
                            )
                    )
            );
            case "statistics" -> new SectionView(
                    "통계 리포트",
                    "가입/탈퇴 현황과 상품 클릭 흐름을 확인합니다.",
                    buildStatisticsTable()
            );
            case "settings" -> new SectionView(
                    "설정",
                    "권한, 배너, 로그, 기본값을 관리합니다.",
                    buildStaticTable(
                            List.of("항목", "설명", "상태", "메모"),
                            List.of(
                                    row("권한 관리", "admin / editor", "사용 중", "기본"),
                                    row("배너 노출", "메인 상단 배너", "활성", "운영"),
                                    row("로그 보관", "30일", "설정됨", "기본값")
                            )
                    )
            );
            default -> new SectionView(
                    "관리자 메인",
                    "유저, 물건, 대출, 공고, 자산을 한눈에 확인하는 관리자 홈입니다.",
                    new TableView(List.of(), List.of())
            );
        };
    }

    private TableView buildUserTable() {
        List<TableRow> rows = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .limit(8)
                .map(user -> row(
                        safe(user.getEmail()),
                        safe(user.getNickname()),
                        roleLabel(user.getRole()),
                        statusLabel(user.getStatus())
                ))
                .toList();
        return new TableView(List.of("이메일", "닉네임", "권한", "상태"), rows);
    }

    private TableView buildSubscriptionTable() {
        List<TableRow> rows = subscriptionRepository.findAll(Sort.by(Sort.Direction.DESC, "announcementId"))
                .stream()
                .limit(8)
                .map(item -> row(
                        safe(item.getTitle()),
                        safe(item.getRegion()),
                        safe(item.getRecuitmentType()),
                        dateRange(item.getApplyStartDate(), item.getApplyEndDate())
                ))
                .toList();
        return new TableView(List.of("제목", "지역", "모집유형", "기간"), rows);
    }

    private TableView buildAnnouncementTable() {
        List<TableRow> rows = announcementRepository.findAll(Sort.by(Sort.Direction.DESC, "announcementId"))
                .stream()
                .limit(8)
                .map(item -> row(
                        safe(item.getTitle()),
                        safe(item.getRegion()),
                        safe(item.getRecuitmentType()),
                        safe(item.getStatus())
                ))
                .toList();
        return new TableView(List.of("제목", "지역", "모집유형", "상태"), rows);
    }

    private TableView buildPolicyTable() {
        List<TableRow> rows = policyRepository.findAll(Sort.by(Sort.Direction.DESC, "policyId"))
                .stream()
                .limit(8)
                .map(item -> row(
                        safe(item.getTitle()),
                        safe(item.getMainCategory()),
                        safe(item.getSubCategory()),
                        safe(item.getStatus())
                ))
                .toList();
        return new TableView(List.of("제목", "대분류", "소분류", "상태"), rows);
    }

    private TableView buildLoanTable() {
        return new TableView(
                List.of("상품", "단계", "상태", "업데이트"),
                List.of(
                        row("청년 버팀목 대출", "계약서 작성 중", "전자서명 대기", "오늘"),
                        row("신혼부부 전세자금대출", "PDF 생성 완료", "검토 필요", "15분 전"),
                        row("중소기업취업청년 전월세보증금", "저장 완료", "진행 중", "31분 전"),
                        row("일반 버팀목전세자금대출", "다음 페이지 이동", "완료", "1시간 전")
                )
        );
    }

    private TableView buildNoticeTable() {
        List<TableRow> rows = noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "noticeId"))
                .stream()
                .limit(8)
                .map(notice -> row(
                        safe(notice.getTitle()),
                        safe(notice.getCategory()),
                        notice.isImportant() ? "중요" : "일반",
                        formatTime(notice.getCreatedAt())
                ))
                .toList();

        return new TableView(List.of("제목", "분류", "구분", "작성일"), rows);
    }

    private TableView buildCommunityTable() {
        List<TableRow> rows = communityPostRepository.findAll(Sort.by(Sort.Direction.DESC, "postId"))
                .stream()
                .limit(8)
                .map(post -> row(
                        safe(post.getTitle()),
                        safe(post.getRegion()),
                        String.valueOf(post.getViewCount()),
                        formatTime(post.getCreatedAt())
                ))
                .toList();

        return new TableView(List.of("제목", "지역", "조회수", "작성일"), rows);
    }

    private TableView buildStatisticsTable() {
        List<User> users = userRepository.findAll();

        long totalUsers = users.size();
        long enabledUsers = users.stream()
                .filter(user -> user.getStatus() == UserStatus.enabled)
                .count();
        long disabledUsers = users.stream()
                .filter(user -> user.getStatus() == UserStatus.disabled)
                .count();

        return new TableView(
                List.of("항목", "값", "설명", "상태"),
                List.of(
                        row("전체 가입 회원", String.valueOf(totalUsers), "전체 가입 유저 수", "대기"),
                        row("활성 회원", String.valueOf(enabledUsers), "현재 활성 상태 회원 수", "대기"),
                        row("탈퇴/비활성 회원", String.valueOf(disabledUsers), "비활성 처리 회원 수", "대기"),
                        row("상품 클릭률", "-", "상품 클릭 로그 또는 클릭 테이블 확인 필요", "대기")
                )
        );
    }

    private TableView buildStaticTable(List<String> headers, List<TableRow> rows) {
        return new TableView(headers, rows);
    }

    private List<MenuItem> buildMenuItems(String section) {
        return List.of(
                menu("관리자 메인", "/admin", "overview".equals(section), "전체 현황"),
                menu("유저", "/admin?section=users", "users".equals(section), "유저 리스트"),
                menu("물건 관리", "/admin/properties", false, "임대 물건 관리"),
                menu("대출", "/admin?section=loan", "loan".equals(section), "대출 리스트"),
                menu("공고", "/admin/announcements", "announcement".equals(section), "공고 리스트"),
                menu("제도", "/admin/policies", "policy".equals(section), "지원제도 리스트"),
                menu("자산", "/admin?section=asset", "asset".equals(section), "파일 관리"),
                menu("커뮤니티", "/admin?section=community", "community".equals(section), "게시글 관리"),
                menu("공지사항", "/admin?section=notice", "notice".equals(section), "공지사항 관리"),
                menu("시뮬레이션", "/admin?section=simulation", "simulation".equals(section), "진단 / 계산"),
                menu("통계 리포트", "/admin?section=statistics", "statistics".equals(section), "로그 / 통계"),
                menu("설정", "/admin?section=settings", "settings".equals(section), "권한 / 시스템")
        );
    }

    private List<StatCard> buildStats() {
        return List.of(
                new StatCard("등록 유저", String.valueOf(userRepository.count()), "전체"),
                new StatCard("임대 물건", String.valueOf(propertyRepository.count()), "전체"),
                new StatCard("노출 공고", String.valueOf(announcementRepository.count()), "전체"),
                new StatCard("노출 제도", String.valueOf(policyRepository.count()), "전체"),
                new StatCard("계약 진행", "27", "오늘")
        );
    }

    private List<OverviewAction> buildOverviewActions() {
        return List.of(
                new OverviewAction("유저 리스트", "가입 유저의 권한과 상태를 확인합니다.", "/admin?section=users"),
                new OverviewAction("물건 관리", "지도와 상세페이지에 노출될 임대 물건을 관리합니다.", "/admin/properties"),
                new OverviewAction("대출 리스트", "대출 계약과 서명 진행 상태를 확인합니다.", "/admin?section=loan"),
                new OverviewAction("공고 리스트", "노출 중인 공고와 모집 유형을 확인합니다.", "/admin/announcements"),
                new OverviewAction("지원제도 리스트", "노출 중인 지원제도의 유형을 확인합니다.", "/admin/policies")
        );
    }

    private List<RecentLog> buildRecentLogs() {
        return List.of(
                new RecentLog("청년 버팀목 대출", "계약서 생성 완료", "2분 전"),
                new RecentLog("전세자금대출", "전자서명 대기", "15분 전"),
                new RecentLog("중소기업취업청년 보증금", "PDF 저장 완료", "31분 전"),
                new RecentLog("일반 버팀목전세자금대출", "다음 페이지 이동", "1시간 전")
        );
    }

    private String normalizeSection(String section) {
        if (section == null || section.isBlank()) {
            return "overview";
        }

        return switch (section) {
            case "overview", "users", "subscription", "loan", "announcement", "policy",
                 "asset", "notice", "community", "simulation", "statistics", "settings" -> section;
            default -> "overview";
        };
    }

    private String sectionLabel(String section) {
        return switch (section) {
            case "users" -> "유저 리스트";
            case "subscription" -> "청약 리스트";
            case "loan" -> "대출 리스트";
            case "announcement" -> "공고 리스트";
            case "policy" -> "지원제도 리스트";
            case "notice" -> "공지사항 관리";
            case "asset" -> "자산 리스트";
            case "community" -> "커뮤니티 리스트";
            case "simulation" -> "시뮬레이션";
            case "statistics" -> "통계 리포트";
            case "settings" -> "설정";
            default -> "관리자 메인";
        };
    }

    private String roleLabel(UserRole role) {
        if (role == null) {
            return "-";
        }

        return switch (role) {
            case ADMIN -> "관리자";
            case USER -> "일반";
        };
    }

    private String statusLabel(UserStatus status) {
        if (status == null) {
            return "-";
        }

        return switch (status) {
            case enabled -> "활성";
            case disabled -> "비활성";
        };
    }

    private String dateRange(LocalDate start, LocalDate end) {
        return formatDate(start) + " ~ " + formatDate(end);
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(DATE_FMT);
    }

    private String formatTime(Instant instant) {
        if (instant == null) {
            return "-";
        }

        return TIME_FMT.format(instant.atZone(ZoneId.of("Asia/Seoul")));
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private MenuItem menu(String label, String href, boolean active, String description) {
        return new MenuItem(label, href, active, description);
    }

    private TableRow row(String... cells) {
        return new TableRow(List.of(cells));
    }

    private record SectionView(String title, String description, TableView tableView) {
    }

    public record MenuItem(String label, String href, boolean active, String description) {
    }

    public record StatCard(String label, String value, String meta) {
    }

    public record OverviewAction(String title, String description, String href) {
    }

    public record RecentLog(String title, String detail, String time) {
    }

    public record TableRow(List<String> columns) {
    }

    public record TableView(List<String> headers, List<TableRow> rows) {
    }
}
