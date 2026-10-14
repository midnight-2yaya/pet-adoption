package com.pet.adoption.service;

import com.pet.adoption.exception.BusinessException;
import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@Service
public class UploadService {
    @Value("${app.minio.bucket}")
    String bucket;
    @Value("${app.minio.public-url}")
    String publicUrl;

    final MinioClient minio;

    static final Set<String> ALLOWED = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"));

    public UploadService(MinioClient minio) {
        this.minio = minio;
    }

    public Map<String, String> upload(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException("请选择图片");
        if (!ALLOWED.contains(file.getContentType())) throw new BusinessException("只支持图片文件");

        String old = file.getOriginalFilename() == null ? "image.jpg" : file.getOriginalFilename();
        String suffix = old.contains(".") ? old.substring(old.lastIndexOf(".")) : ".jpg";
        String folder = LocalDate.now().toString();
        String name = UUID.randomUUID().toString().replace("-", "") + suffix;
        String object = folder + "/" + name;

        try {
            boolean exists = minio.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minio.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\","
                        + "\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],"
                        + "\"Resource\":[\"arn:aws:s3:::" + bucket + "/*\"]}]}";
                minio.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
            }
            minio.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            Map<String, String> m = new LinkedHashMap<>();
            m.put("path", "/" + object);
            m.put("url", publicUrl + "/" + object);
            return m;
        } catch (Exception e) {
            throw new BusinessException("上传失败：" + e.getMessage());
        }
    }
}