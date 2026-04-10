package com.yvliangbao.common.service.common;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 阿里云OSS文件上传服务接口
 *
 * @author 余量宝
 */
public interface OssService {

    /**
     * 获取前端直传签名
     * 前端使用签名直接上传文件到OSS，避免通过后端中转
     *
     * @param dir 上传目录（如：images/）
     * @return 签名信息
     */
    Map<String, String> getUploadSignature(String dir);

    /**
     * 上传文件（服务端上传方式）
     *
     * @param file 文件
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * 上传文件（服务端上传方式）
     *
     * @param inputStream 文件流
     * @param originalFileName 原始文件名
     * @param contentType 文件类型
     * @return 文件访问URL
     */
    String uploadFile(InputStream inputStream, String originalFileName, String contentType);

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL或路径
     */
    void deleteFile(String fileUrl);

    /**
     * 判断文件是否存在
     *
     * @param fileUrl 文件URL或路径
     * @return 是否存在
     */
    boolean fileExists(String fileUrl);

    /**
     * 生成文件访问URL
     *
     * @param filePath 文件路径
     * @return 完整URL
     */
    String getFileUrl(String filePath);

    /**
     * 上传文件（带目录）
     *
     * @param file 文件
     * @param dir 目录
     * @return 文件访问URL
     */
    default String upload(MultipartFile file, String dir) throws IOException {
        return uploadFile(file);
    }

    /**
     * 上传Base64图片
     *
     * @param base64Data Base64数据
     * @param dir 目录
     * @return 文件访问URL
     */
    default String uploadBase64(String base64Data, String dir) {
        // 简单实现：提取base64内容并上传
        if (base64Data == null || !base64Data.contains(",")) {
            return null;
        }
        String base64Content = base64Data.split(",")[1];
        byte[] bytes = java.util.Base64.getDecoder().decode(base64Content);
        String fileName = dir + "/" + System.currentTimeMillis() + ".png";
        // 这里需要具体实现，暂时返回null
        return null;
    }
}
