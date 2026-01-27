package com.code808.calmdesk.domain.company.service;

import com.code808.calmdesk.domain.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class CompanyCodeGenerator {
    private static final String CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 6;
    private static final int MAX_RETRY = 5;

    private final CompanyRepository companyRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    private String generateRandomCode(){
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++){
            int index = secureRandom.nextInt(CHARS.length());
            code.append(CHARS.charAt(index));
        }
        return code.toString();
    }

    public String generateUniqueCode(){
        for (int i=0; i<MAX_RETRY; i++){
            String code = generateRandomCode();

            if(!companyRepository.existsByCompanyCode(code)) {
                return code;
            }
        }
        throw new RuntimeException("회사 코드 생성에 실패했습니다. 다시 시도해주세요.");
    }
}
