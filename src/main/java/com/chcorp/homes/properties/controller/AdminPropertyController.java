package com.chcorp.homes.properties.controller;

import com.chcorp.homes.properties.dto.AdminPropertyListDTO;
import com.chcorp.homes.properties.dto.AdminPropertyRequestDTO;
import com.chcorp.homes.properties.entity.Property;
import com.chcorp.homes.properties.service.AdminPropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/properties")
public class AdminPropertyController {

    private final AdminPropertyService adminPropertyService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "") String category,
                       @RequestParam(defaultValue = "") String dealType,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<AdminPropertyListDTO> result = adminPropertyService.getList(keyword, category, dealType, page, 10);

        model.addAttribute("properties", result);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("dealType", dealType);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("startPage", Math.max(0, page - 2));
        model.addAttribute("endPage", Math.min(Math.max(result.getTotalPages() - 1, 0), page + 2));

        return "admin/properties/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new AdminPropertyRequestDTO());
        model.addAttribute("isEdit", false);
        return "admin/properties/form";
    }

    @PostMapping
    public String create(@ModelAttribute("form") AdminPropertyRequestDTO dto,
                         Model model) {
        try {
            adminPropertyService.register(dto);
            return "redirect:/admin/properties";
        } catch (RuntimeException e) {
            model.addAttribute("isEdit", false);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/properties/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Property property = adminPropertyService.getOne(id);

        model.addAttribute("form", toForm(property));
        model.addAttribute("isEdit", true);
        model.addAttribute("propertyId", id);
        model.addAttribute("thumbnailPreviewUrl", adminPropertyService.resolveThumbnailPreviewUrl(id, property.getThumbnailUrl()));
        return "admin/properties/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") AdminPropertyRequestDTO dto,
                         Model model) {
        try {
            adminPropertyService.update(id, dto);
            return "redirect:/admin/properties";
        } catch (RuntimeException e) {
            model.addAttribute("isEdit", true);
            model.addAttribute("propertyId", id);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/properties/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminPropertyService.delete(id);
        return "redirect:/admin/properties";
    }

    private AdminPropertyRequestDTO toForm(Property property) {
        AdminPropertyRequestDTO form = new AdminPropertyRequestDTO();
        form.setTitle(property.getTitle());
        form.setAddress(property.getAddress());
        form.setRegion(property.getRegion());
        form.setLandlordUserId(property.getLandlordUserId());
        form.setCategory(property.getCategory());
        form.setDealType(property.getDealType());
        form.setDepositAmount(property.getDepositAmount());
        form.setMonthlyRentAmount(property.getMonthlyRentAmount());
        form.setMaintenanceFee(property.getMaintenanceFee());
        form.setRoomType(property.getRoomType());
        form.setExclusiveAreaM2(property.getExclusiveAreaM2());
        form.setSupplyAreaM2(property.getSupplyAreaM2());
        form.setRoomCount(property.getRoomCount());
        form.setBathroomCount(property.getBathroomCount());
        form.setFloor(property.getFloor());
        form.setTotalFloor(property.getTotalFloor());
        form.setDirection(property.getDirection());
        form.setHeatingType(property.getHeatingType());
        form.setElevatorAvailable(property.getElevatorAvailable());
        form.setTotalParkingCount(property.getTotalParkingCount());
        form.setBuildingUse(property.getBuildingUse());
        form.setMoveInType(property.getMoveInType());
        form.setMoveInDate(property.getMoveInDate());
        form.setApprovalDate(property.getApprovalDate());
        form.setFirstRegistrationDate(property.getFirstRegistrationDate());
        form.setTag(property.getTag());
        form.setOptions(property.getOptions());
        form.setSecurityFacilities(property.getSecurityFacilities());
        form.setThumbnailUrl(property.getThumbnailUrl());
        form.setDescription(property.getDescription());
        return form;
    }
}
