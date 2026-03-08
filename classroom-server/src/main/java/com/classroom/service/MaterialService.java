package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.entity.Material;

public interface MaterialService extends IService<Material> {
    IPage<Material> pageList(int page, int size, Long courseId);
    void incrementDownload(Long id);
}
