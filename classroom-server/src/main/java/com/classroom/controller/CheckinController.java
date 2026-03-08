package com.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.classroom.common.result.R;
import com.classroom.dto.CheckinDTO;
import com.classroom.entity.Checkin;
import com.classroom.entity.CheckinRecord;
import com.classroom.service.CheckinRecordService;
import com.classroom.service.CheckinService;
import com.classroom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;
    private final CheckinRecordService checkinRecordService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public R<Checkin> create(@Valid @RequestBody CheckinDTO dto) {
        return R.ok(checkinService.createCheckin(dto));
    }

    @PutMapping("/{id}/end")
    @PreAuthorize("hasRole('TEACHER')")
    public R<Void> end(@PathVariable Long id) {
        checkinService.endCheckin(id);
        return R.ok();
    }

    @GetMapping("/page")
    public R<IPage<Checkin>> page(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) Long courseId,
                                   @RequestParam(required = false) Integer status) {
        return R.ok(checkinService.pageList(page, size, courseId, status));
    }

    @PostMapping("/{checkinId}/do")
    @PreAuthorize("hasRole('STUDENT')")
    public R<Void> doCheckin(@PathVariable Long checkinId,
                              @RequestParam(required = false) Double latitude,
                              @RequestParam(required = false) Double longitude) {
        checkinRecordService.doCheckin(checkinId, SecurityUtil.getCurrentUserId(), latitude, longitude);
        return R.ok();
    }

    @GetMapping("/{checkinId}/records")
    public R<IPage<CheckinRecord>> records(@PathVariable Long checkinId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int size,
                                            @RequestParam(required = false) Integer status) {
        return R.ok(checkinRecordService.pageList(page, size, checkinId, status));
    }

    @GetMapping("/stats/{courseId}")
    public R<Map<String, Object>> stats(@PathVariable Long courseId) {
        return R.ok(checkinRecordService.getCheckinStats(courseId));
    }
}
