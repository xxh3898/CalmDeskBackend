package com.code808.calmdesk.domain.vacation.controller.admin;

import com.code808.calmdesk.domain.attendance.service.EmployeeAttendanceService;
import com.code808.calmdesk.domain.company.service.CompanyService;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/applications")
@RequiredArgsConstructor
public class AdminVacationController {

    private final MemberRepository memberRepository;
    private final EmployeeAttendanceService employeeAttendanceService;
    private final CompanyService companyService;

//    @GetMapping("/leaves")
//    public ResponseEntity<List<LeaveRequestItemRes>> getLeaves(Principal principal) {
//        var member = memberRepository.findEmailWithDetails(principal.getName())
//                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
//        if (member.getCompany() == null) {
//            return ResponseEntity.ok(List.of());
//        }
//        List<LeaveRequestItemRes> list = employeeAttendanceService.getLeaveRequestsByCompany(member.getCompany().getCompanyId());
//        return ResponseEntity.ok(list);
//    }
}
