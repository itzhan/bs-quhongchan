package com.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.entity.Material;
import com.classroom.mapper.MaterialMapper;
import com.classroom.service.MaterialService;
import org.springframework.stereotype.Service;

@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {

    @Override
    public IPage<Material> pageList(int page, int size, Long courseId) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) wrapper.eq(Material::getCourseId, courseId);
        wrapper.orderByDesc(Material::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public void incrementDownload(Long id) {
        Material material = getById(id);
        if (material != null) {
            material.setDownloadCount(material.getDownloadCount() + 1);
            updateById(material);
        }
    }
}
