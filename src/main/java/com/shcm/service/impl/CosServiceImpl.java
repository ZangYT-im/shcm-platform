//package com.shcm.service.impl;
//
//import com.qcloud.cos.COSClient;
//import com.qcloud.cos.ClientConfig;
//import com.qcloud.cos.auth.BasicCOSCredentials;
//import com.qcloud.cos.auth.COSCredentials;
//import com.qcloud.cos.http.HttpProtocol;
//import com.qcloud.cos.model.Bucket;
//import com.qcloud.cos.region.Region;
//import com.shcm.service.CosService;
//import com.shcm.utils.ConstantPropertiesUtils;
//import org.joda.time.DateTime;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.InputStream;
//import java.util.List;
//import java.util.UUID;
//
//public class CosServiceImpl implements CosService {
//    @Override
//    public String uploadFile(MultipartFile file) {
//        // 1 初始化用户身份信息（secretId, secretKey）。
//        // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
//        String secretId = ConstantPropertiesUtils.ACCESS_KEY_ID;
//        String secretKey = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
//
//        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
//        // 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
//        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
//        Region region = new Region("ap-shanghai");
//        ClientConfig clientConfig = new ClientConfig(region);
//        // 这里建议设置使用 https 协议
//        // 从 5.6.54 版本开始，默认使用了 https
//        clientConfig.setHttpProtocol(HttpProtocol.https);
//        // 3 生成 cos 客户端。
//        COSClient cosClient = new COSClient(cred, clientConfig);
//
//        List<Bucket> buckets = cosClient.listBuckets();
//        for (Bucket bucketElement : buckets) {
//            String bucketName = bucketElement.getName();
//            String bucketLocation = bucketElement.getLocation();
//            System.out.println(bucketName + "--" + bucketLocation);
//        }
//        return "";
//    }
//
////
////    //上传头像到oss
////    @Override
////    public String uploadFileAvatar(MultipartFile file) {
////        // 工具类获取值
////        String endpoint = ConstantPropertiesUtils.END_POINT;
////        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
////        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
////        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;
////
////        try {
////            // 创建OSS实例。
////            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
////
////            //获取上传文件输入流
////            InputStream inputStream = file.getInputStream();
////            //获取文件名称
////            String fileName = file.getOriginalFilename();
////
////            //1 在文件名称里面添加随机唯一的值
////            String uuid = UUID.randomUUID().toString().replaceAll("-","");
////            // yuy76t5rew01.jpg
////            fileName = uuid+fileName;
////
////            //2 把文件按照日期进行分类
////            //获取当前日期
////            //   2019/11/12
////            String datePath = new DateTime().toString("yyyy/MM/dd");
////            //拼接
////            //  2019/11/12/ewtqr313401.jpg
////            fileName = datePath+"/"+fileName;
////
////            //调用oss方法实现上传
////            //第一个参数  Bucket名称
////            //第二个参数  上传到oss文件路径和文件名称   aa/bb/1.jpg
////            //第三个参数  上传文件输入流
////            ossClient.putObject(bucketName,fileName , inputStream);
////
////            // 关闭OSSClient。
////            ossClient.shutdown();
////
////            //把上传之后文件路径返回
////            //需要把上传到阿里云oss路径手动拼接出来
////            //  https://edu-guli-1010.oss-cn-beijing.aliyuncs.com/01.jpg
////            String url = "https://"+bucketName+"."+endpoint+"/"+fileName;
////            return url;
////        }catch(Exception e) {
////            e.printStackTrace();
////            return null;
////        }
////    }
//
//}
