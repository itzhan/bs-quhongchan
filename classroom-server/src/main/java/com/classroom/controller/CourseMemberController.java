package com.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.classroom.common.result.R;
import com.classroom.entity.CourseMember;
import com.classroom.mapper.CourseMemberMapper;
import com.classroom.service.CourseMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course-member")
@RequiredArgsConstructor
public class CourseMemberController {

    private final CourseMemberMapper courseMemberMapper;
    private final CourseMemberService courseMemberService;

    @GetMapping("/page")
    public R<IPage<CourseMember>> page(@RequestParam(defaultValue = "1") int current,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) Long courseId) {
        return R.ok(courseMemberMapper.selectPageWithName(new Page<>(current, size), courseId));
    }

    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        courseMemberService.removeById(id);
        return R.ok();
    }
}
