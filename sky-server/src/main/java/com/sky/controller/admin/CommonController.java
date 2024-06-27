package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {


    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){//接收上传文件的类型
        log.info("文件上传:file就是上传的文件！");
        log.info("文件的原始名字:"+file.getOriginalFilename());

        String url = null;
        //文件字节数组
        try {
            byte[] bytes = file.getBytes();
            //调用阿里云的 工具方法
//            aliOssUtil.upload(文件的字节数组，文件名字);
            String originalFilename = file.getOriginalFilename();
            // 原始文件  8.png
            // 两个不同的用户 传两种不同图片 但是 名字一样
            // 需要将文件名字组合成一个 唯一的名字
            String fileName = UUID.randomUUID().toString()+originalFilename;

            url = aliOssUtil.upload(bytes, fileName);
        } catch (IOException e) {
            log.info("文件上传异常");
            return Result.error("文件上传异常");
        }

        return Result.success(url);
    }

}
