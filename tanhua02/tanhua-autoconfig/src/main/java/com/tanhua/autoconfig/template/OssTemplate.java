package com.tanhua.autoconfig.template;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.tanhua.autoconfig.properties.OssProperties;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OssTemplate {

    private OssProperties ossProperties;

    public OssTemplate(OssProperties ossProperties){
        this.ossProperties = ossProperties;
    }

    /**
     * 上传文件
     * @param filename
     * @param is
     * @return
     */
    public String upload(String filename, InputStream is) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ossProperties.getEndpoint();
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = ossProperties.getAccessKeyId();
        String accessKeySecret = ossProperties.getAccessKeySecret();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 截取文件的后缀名
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 构建yyyy/MM/dd的路径
        String ymd = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        // 组成上传到bucket的文件路径 images/2021/10/06/asdfasdfasd.png
        String name = "images/" + ymd + "/" + UUID.randomUUID().toString() + suffix;

        // 上传文件流。
        ossClient.putObject(ossProperties.getBucketName(), name, is);

        // 关闭OSSClient。
        ossClient.shutdown();
        // 返回图片的完整路径
        return ossProperties.getUrl() + "/" + name;
    }
}