package com.code808.calmdesk.domain.company.service;

import com.code808.calmdesk.domain.company.dto.CompanyDto;

public interface CompanyService {
    CompanyDto.CodeResponse generateCode();
    CompanyDto.RegisterResponse register(CompanyDto.RegisterRequest request, String email);

}
