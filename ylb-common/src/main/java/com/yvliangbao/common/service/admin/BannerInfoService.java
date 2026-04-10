package com.yvliangbao.common.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.entity.admin.BannerInfo;


import java.util.List;

/**
 * 轮播图 Service
 *
 * @author 余量宝
 */
public interface BannerInfoService extends IService<BannerInfo> {

    /**
     * 获取启用的轮播图列表（按排序）
     *
     * @return 轮播图列表
     */
    List<BannerInfo> listEnabled();

    /**
     * 获取所有轮播图列表（管理用）
     *
     * @return 轮播图列表
     */
    List<BannerInfo> listAll();

    /**
     * 分页查询轮播图
     *
     * @param page 页码
     * @param size 每页数量
     * @return 分页数据
     */
    IPage<BannerInfo> pageList(int page, int size);

    /**
     * 创建轮播图
     *
     * @param banner 轮播图信息
     * @return 轮播图ID
     */
    Long createBanner(BannerInfo banner);

    /**
     * 更新轮播图
     *
     * @param banner 轮播图信息
     */
    void updateBanner(BannerInfo banner);

    /**
     * 删除轮播图
     *
     * @param id 轮播图ID
     */
    void deleteBanner(Long id);

    /**
     * 更新轮播图状态
     *
     * @param id     轮播图ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);
}
