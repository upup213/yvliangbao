package com.yvliangbao.common.service.impl.common;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.PutObjectResult;
import com.yvliangbao.common.config.OssProperties;
import com.yvliangbao.common.service.common.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 阿里云OSS文件上传服务实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssServiceImpl implements OssService {

    private final OSS ossClient;
    private final OssProperties ossProperties;

    @Override
    public Map<String, String> getUploadSignature(String dir) {
        // 确保目录格式正确
        if (dir == null || dir.isEmpty()) {
            dir = "";
        } else if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        
        String host = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint();
        
        // 设置签名过期时间（1小时）
        long expireEndTime = System.currentTimeMillis() + 3600 * 1000;
        Date expiration = new Date(expireEndTime);
        
        // 设置上传策略
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000); // 最大1GB
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
        
        // 生成签名
        String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = ossClient.calculatePostSignature(postPolicy);
        
        // 返回签名信息
        Map<String, String> result = new HashMap<>();
        result.put("accessid", ossProperties.getAccessKeyId());
        result.put("policy", encodedPolicy);
        result.put("signature", postSignature);
        result.put("dir", dir);
        result.put("host", host);
        result.put("expire", String.valueOf(expireEndTime / 1000));
        
        return result;
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
    }

    @Override
    public String uploadFile(InputStream inputStream, String originalFileName, String contentType) {
        // 生成文件路径：日期/UUID.扩展名
        String filePath = generateFilePath(originalFileName);
        
        // 设置文件元信息
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        
        // 上传文件
        PutObjectResult result = ossClient.putObject(
                ossProperties.getBucketName(),
                filePath,
                inputStream,
                metadata
        );
        
        log.info("文件上传成功, path: {}, etag: {}", filePath, result.getETag());
        
        // 返回访问URL
        return getFileUrl(filePath);
    }

    @Override
    public void deleteFile(String fileUrl) {
        String filePath = extractFilePath(fileUrl);
        if (filePath != null) {
            ossClient.deleteObject(ossProperties.getBucketName(), filePath);
            log.info("文件删除成功: {}", filePath);
        }
    }

    @Override
    public boolean fileExists(String fileUrl) {
        String filePath = extractFilePath(fileUrl);
        if (filePath == null) {
            return false;
        }
        return ossClient.doesObjectExist(ossProperties.getBucketName(), filePath);
    }

    @Override
    public String getFileUrl(String filePath) {
        // 如果配置了自定义域名（CDN），使用自定义域名
        if (ossProperties.getDomain() != null && !ossProperties.getDomain().isEmpty()) {
            return ossProperties.getDomain() + "/" + filePath;
        }
        // 否则使用默认域名
        return "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + filePath;
    }

    /**
     * 生成文件存储路径
     *
     * @param originalFileName 原始文件名
     * @return 存储路径
     */
    private String generateFilePath(String originalFileName) {
        // 获取文件扩展名
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        // 生成路径：日期/UUID.扩展名
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        
        return datePath + "/" + fileName;
    }

    /**
     * 从URL中提取文件路径
     *
     * @param fileUrl 文件URL
     * @return 文件路径
     */
    private String extractFilePath(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        
        // 如果已经是相对路径，直接返回
        if (!fileUrl.startsWith("http")) {
            return fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
        }
        
        // 从URL中提取路径
        try {
            int idx = fileUrl.indexOf(".com/");
            if (idx > 0) {
                return fileUrl.substring(idx + 5);
            }
        } catch (Exception e) {
            log.warn("解析文件URL失败: {}", fileUrl);
        }
        
        return null;
    }
}
