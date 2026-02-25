package com.code808.calmdesk.domain.company.service;

import com.code808.calmdesk.domain.company.dto.CompanyDto;

import java.util.List;

public interface CompanyService {
    CompanyDto.CodeResponse generateCode();
    CompanyDto.RegisterResponse register(CompanyDto.RegisterRequest request, String email);
    CompanyDto.CheckResponse getByCode(String CompanyCode);
    CompanyDto.JoinResponse join(CompanyDto.JoinRequest request, String email);
    List<CompanyDto.JoinListItemRes> listAllJoins(Long companyId);
    void approveJoin(Long memberId, String adminEmail);
    void rejectJoin(Long memberId, String adminEmail);
    void createJoinRequestFromBusinessCard(String adminEmail, String name, String email, String phone,
                                           Long departmentId, Long rankId);
}
