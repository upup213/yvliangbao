package com.yvliangbao.common.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置
 *
 * @author 余量宝
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OssConfig {

    private final OssProperties ossProperties;

    /**
     * 创建OSS客户端Bean
     */
    @Bean
    public OSS ossClient() {
        log.info("初始化OSS客户端, endpoint: {}, bucket: {}", 
                ossProperties.getEndpoint(), 
                ossProperties.getBucketName());
        
        return new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
    }
}
