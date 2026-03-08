package com.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.classroom.common.result.R;
import com.classroom.entity.Announcement;
import com.classroom.service.AnnouncementService;
import com.classroom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public R<Void> create(@RequestBody Announcement announcement) {
        announcement.setPublisherId(SecurityUtil.getCurrentUserId());
        announcementService.save(announcement);
        return R.ok();
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public R<Void> update(@RequestBody Announcement announcement) {
        announcementService.updateById(announcement);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public R<Void> delete(@PathVariable Long id) {
        announcementService.removeById(id);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<Announcement> getById(@PathVariable Long id) {
        return R.ok(announcementService.getById(id));
    }

    @GetMapping("/page")
    public R<IPage<Announcement>> page(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) Long courseId,
                                        @RequestParam(required = false) Integer type,
                                        @RequestParam(required = false) Integer status) {
        return R.ok(announcementService.pageList(page, size, courseId, type, status));
    }
}
