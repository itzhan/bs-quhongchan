package com.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.classroom.common.result.R;
import com.classroom.entity.CheckinRecord;
import com.classroom.mapper.CheckinRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkin-record")
@RequiredArgsConstructor
public class CheckinRecordController {

    private final CheckinRecordMapper checkinRecordMapper;

    @GetMapping("/page")
    public R<IPage<CheckinRecord>> page(@RequestParam(defaultValue = "1") int current,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) Long checkinId,
                                         @RequestParam(required = false) Long courseId,
                                         @RequestParam(required = false) Integer status) {
        return R.ok(checkinRecordMapper.selectPageWithFilters(new Page<>(current, size), checkinId, courseId, status));
    }
}
