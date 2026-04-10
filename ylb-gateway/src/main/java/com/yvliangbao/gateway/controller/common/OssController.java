package com.yvliangbao.gateway.controller.common;


import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.common.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OSS文件上传控制器（共用）
 *
 * @author 余量宝
 */
@Slf4j
@RestController
@RequestMapping("/oss")
@RequiredArgsConstructor
@Api(tags = "共用-文件上传")
public class OssController {

    private final OssService ossService;

    @GetMapping("/signature")
    @ApiOperation("获取上传签名（前端直传）")
    public Result<Map<String, String>> getUploadSignature(
            @ApiParam("上传目录") @RequestParam(value = "dir", defaultValue = "") String dir) {
        Map<String, String> signature = ossService.getUploadSignature(dir);
        return Result.success(signature);
    }

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public Result<Map<String, String>> upload(
            @ApiParam("文件") @RequestParam("file") MultipartFile file,
            @ApiParam("上传目录") @RequestParam(value = "dir", defaultValue = "") String dir) throws IOException {
        String url = ossService.upload(file, dir);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        data.put("name", file.getOriginalFilename());
        return Result.success(data);
    }

    @PostMapping("/upload/base64")
    @ApiOperation("上传Base64图片")
    public Result<String> uploadBase64(@RequestBody Map<String, String> params) {
        String base64 = params.get("base64");
        String dir = params.getOrDefault("dir", "");
        String url = ossService.uploadBase64(base64, dir);
        return Result.success(url);
    }
}
