package com.yvliangbao.common.service.impl.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yvliangbao.common.mapper.admin.BannerInfoMapper;
import com.yvliangbao.common.pojo.entity.admin.BannerInfo;
import com.yvliangbao.common.service.admin.BannerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图 Service 实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class BannerInfoServiceImpl extends ServiceImpl<BannerInfoMapper, BannerInfo> implements BannerInfoService {

    @Override
    public List<BannerInfo> listEnabled() {
        return this.lambdaQuery()
                .eq(BannerInfo::getStatus, 1)
                .orderByAsc(BannerInfo::getSort)
                .list();
    }

    @Override
    public List<BannerInfo> listAll() {
        return this.lambdaQuery()
                .orderByAsc(BannerInfo::getSort)
                .list();
    }

    @Override
    public IPage<BannerInfo> pageList(int page, int size) {
        return this.lambdaQuery()
                .orderByAsc(BannerInfo::getSort)
                .page(new Page<>(page, size));
    }

    @Override
    public Long createBanner(BannerInfo banner) {
        this.save(banner);
        return banner.getId();
    }

    @Override
    public void updateBanner(BannerInfo banner) {
        this.updateById(banner);
    }

    @Override
    public void deleteBanner(Long id) {
        this.removeById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        this.lambdaUpdate()
                .eq(BannerInfo::getId, id)
                .set(BannerInfo::getStatus, status)
                .update();
    }
}
