package com.code808.calmdesk.domain.company.controller;

import com.code808.calmdesk.domain.company.dto.CompanyDto;
import com.code808.calmdesk.domain.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Company", description = "회사 관리 API (등록, 코드 조회, 참여)")
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "회사 코드 생성", description = "회사 등록 시 사용할 임시 난수 코드를 생성합니다.")
    @GetMapping("/genderate-code")
    public ResponseEntity<CompanyDto.CodeResponse> generateCode() {
        return ResponseEntity.ok(companyService.generateCode());
    }

    @Operation(summary = "회사 등록", description = "새로운 회사를 등록하고 관리자 권한을 부여합니다.")
    @PostMapping("/register")
    public ResponseEntity<CompanyDto.RegisterResponse> register(
            @Valid @RequestBody CompanyDto.RegisterRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(companyService.register(request, email));
    }

    @Operation(summary = "코드 기반 회사 조회", description = "회사 코드로 회사 정보(부서, 직급 목록 등)를 조회합니다.")
    @GetMapping("/by-code/{companyCode}")
    public ResponseEntity<CompanyDto.CheckResponse> generateByCode(
            @Parameter(description = "회사 코드", example = "ABC12345") @Valid @PathVariable("companyCode") String companyCode) {
        return ResponseEntity.ok(companyService.getByCode(companyCode));
    }

    @Operation(summary = "회사 참여 신청", description = "특정 회사와 부서, 직급을 선택하여 입사 신청을 합니다.")
    @PostMapping("/join")
    public ResponseEntity<CompanyDto.JoinResponse> join(
            @Valid @RequestBody CompanyDto.JoinRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(companyService.join(request, email));
    }

}
