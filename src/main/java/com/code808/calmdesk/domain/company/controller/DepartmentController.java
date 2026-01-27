package com.code808.calmdesk.domain.company.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code808.calmdesk.domain.company.dto.DepartmentMemberDto;
import com.code808.calmdesk.domain.company.dto.DepartmentResponseDto;
import com.code808.calmdesk.domain.company.service.DepartmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponseDto> getDepartmentDetails(@PathVariable Long departmentId) {
        DepartmentResponseDto departmentInfo = departmentService.getDepartmentDetails(departmentId);
        return ResponseEntity.ok(departmentInfo);
    }

    @GetMapping("/{departmentId}/members")
    public ResponseEntity<List<DepartmentMemberDto>> getDepartmentMembers(@PathVariable Long departmentId) {
        List<DepartmentMemberDto> members = departmentService.getDepartmentMembers(departmentId);
        return ResponseEntity.ok(members);
    }
}
