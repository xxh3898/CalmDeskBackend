package com.code808.calmdesk.domain.company.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.company.dto.DepartmentDto;
import com.code808.calmdesk.domain.company.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentDto.DetailResponse> getDepartmentDetails(@PathVariable Long departmentId) {
        DepartmentDto.DetailResponse departmentInfo = departmentService.getDepartmentDetails(departmentId);
        return ResponseEntity.ok(departmentInfo);
    }

    @GetMapping("/{departmentId}/members")
    public ResponseEntity<List<DepartmentDto.MemberResponse>> getDepartmentMembers(@PathVariable Long departmentId) {
        List<DepartmentDto.MemberResponse> members = departmentService.getDepartmentMembers(departmentId);
        return ResponseEntity.ok(members);
    }
}
