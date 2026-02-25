package com.code808.calmdesk.domain.businesscard.port.impl;

/**
 * 명함 이미지 추출용 공통 프롬프트.
 * 한글 명함, "Phone"/"Email"/"Address" 등 레이블이 붙은 경우에도 값만 추출하도록 지시.
 */
public final class BusinessCardExtractionPrompt {

    public static final String EXTRACT_PROMPT = """
        이 이미지는 명함입니다. **이미지에 실제로 인쇄된 글자만** 그대로 추출해 JSON으로만 답하세요.
        
        **절대 규칙:**
        - 예시 값·플레이스홀더·추측 값을 넣지 마세요. (010-0000-0000, example.com, 가나다@..., "한국의 대표" 등 금지)
        - name에는 **명함에 적힌 사람 이름만** 넣으세요. 직책(대리, 과장, 대표 등)이나 회사명을 name에 넣지 마세요.
        - phone, mobile에는 명함에 적힌 **실제 전화번호만** (숫자와 하이픈). "Phone", "Tel" 같은 레이블은 제거.
        - email에는 명함에 적힌 **실제 이메일 주소만** (@ 포함). "Email" 레이블 제거. example.com 같은 예시 도메인 금지.
        - address에는 명함에 적힌 **실제 주소만**. "Address", "주소" 레이블 제거.
        - 해당 항목이 이미지에 없으면 null. 반드시 아래 키만 사용한 JSON 한 덩어리만 출력하고, 설명·마크다운 없이 JSON만 출력하세요.

        {
          "name": "이름(사람 이름만)",
          "company": "회사명",
          "department": "부서",
          "title": "직책",
          "phone": "전화번호 값만",
          "mobile": "휴대폰 값만",
          "email": "이메일 값만",
          "address": "주소 값만",
          "fax": "팩스",
          "website": "웹사이트"
        }
        """;

    private BusinessCardExtractionPrompt() {}
}
