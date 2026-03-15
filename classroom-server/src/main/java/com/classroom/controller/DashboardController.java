package com.classroom.controller;

import com.classroom.common.result.R;
import com.classroom.service.DashboardService;
import com.classroom.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public R<DashboardVO> getStats() {
        return R.ok(dashboardService.getStats());
    }
}
