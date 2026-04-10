package com.yvliangbao.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置属性
 *
 * @author 余量宝
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.oss")
public class OssProperties {

    /**
     * OSS节点地址，如：oss-cn-beijing.aliyuncs.com
     */
    private String endpoint;

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * Bucket名称
     */
    private String bucketName;

    /**
     * 访问域名（可选，用于CDN加速）
     */
    private String domain;
}
