package com.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.classroom.common.result.R;
import com.classroom.entity.Material;
import com.classroom.service.MaterialService;
import com.classroom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/material")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @Value("${upload.path:./uploads/}")
    private String uploadPath;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public R<Void> create(@RequestBody Material material) {
        material.setUploaderId(SecurityUtil.getCurrentUserId());
        materialService.save(material);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public R<Void> delete(@PathVariable Long id) {
        materialService.removeById(id);
        return R.ok();
    }

    @GetMapping("/page")
    public R<IPage<Material>> page(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) Long courseId) {
        return R.ok(materialService.pageList(page, size, courseId));
    }

    @PutMapping("/{id}/download")
    public R<Material> download(@PathVariable Long id) {
        materialService.incrementDownload(id);
        return R.ok(materialService.getById(id));
    }
}
