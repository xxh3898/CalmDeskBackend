package com.code808.calmdesk.domain.company.controller;

import com.code808.calmdesk.domain.company.dto.CompanyDto;
import com.code808.calmdesk.domain.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping("/genderate-code")
    public ResponseEntity<CompanyDto.CodeResponse> generateCode() {
        return ResponseEntity.ok(companyService.generateCode());
    }

    @PostMapping("/register")
    public ResponseEntity<CompanyDto.RegisterResponse> register(
            @Valid @RequestBody CompanyDto.RegisterRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(companyService.register(request, email));
    }

    @GetMapping("/by-code/{companyCode}")
    public ResponseEntity<CompanyDto.CheckResponse> generateByCode(
            @Valid @PathVariable("companyCode") String companyCode){
        return ResponseEntity.ok(companyService.getByCode(companyCode));
    }

    @PostMapping("/join")
    public ResponseEntity<CompanyDto.JoinResponse> join(
            @Valid @RequestBody CompanyDto.JoinRequest request,
            Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(companyService.join(request, email));
    }

}
