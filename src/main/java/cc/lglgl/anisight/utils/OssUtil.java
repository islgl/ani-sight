package cc.lglgl.anisight.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author lgl
 */
@Data
@Component
public class OssUtil {
    private final OSS ossClient;

    private final String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private final String region = "cn-beijing";
    private final String bucketName = "ani-sight";
    private final String url = "https://oss.lewisliugl.cn/";

    public OssUtil() {
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(System.getenv("ALIYUN_ACCESS_KEY_ID"),
                System.getenv("ALIYUN_ACCESS_KEY_SECRET"));
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();
    }

    public void shutdown() {
        ossClient.shutdown();
    }

    /**
     * 上传图片至OSS
     *
     * @param filename 图片在OSS上的文件名
     * @param base64   图片的Base64编码
     * @param dir      图片在OSS上的目录
     * @return 上传成功返回true，否则返回false
     */
    public boolean uploadImage(String filename, String base64, String dir) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, dir + "/" + filename, is);
            ossClient.putObject(putObjectRequest);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除OSS上的图片
     *
     * @param filename 图片在OSS上的文件名
     * @param dir      图片在OSS上的目录
     * @return 删除成功返回true，否则返回false
     */
    public boolean deleteImage(String filename, String dir) {
        try {
            ossClient.deleteObject(bucketName, dir + filename);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 批量删除OSS上的图片
     *
     * @param filenames 图片在OSS上的文件名列表
     * @param dir       图片在OSS上的目录
     * @return 删除成功返回true，否则返回false
     */
    public boolean deleteImages(List<String> filenames, String dir) {
        filenames.replaceAll(s -> dir + s);
        try {
            DeleteObjectsResult deleteObjectsResult = ossClient
                    .deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(filenames).withEncodingType("url"));
            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
            return deletedObjects.size() == filenames.size();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除OSS指定目录下的所有图片
     *
     * @param dir 待删除的图片所在目录
     * @return 删除成功返回true，否则返回false
     */
    public boolean deleteAllImages(String dir) {
        if (!dir.endsWith("/")) {
            return false;
        }
        try {
            // 列举所有包含指定前缀的文件并删除。
            String nextMarker = null;
            ObjectListing objectListing = null;
            do {
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName)
                        .withPrefix(dir)
                        .withMarker(nextMarker);

                objectListing = ossClient.listObjects(listObjectsRequest);
                if (objectListing.getObjectSummaries().size() > 0) {
                    List<String> keys = new ArrayList<String>();
                    for (OSSObjectSummary s : objectListing.getObjectSummaries()) {
                        System.out.println("key name: " + s.getKey());
                        keys.add(s.getKey());
                    }
                    keys.remove(dir);
                    if (keys.isEmpty()) {
                        return true;
                    }
                    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keys)
                            .withEncodingType("url");
                    DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(deleteObjectsRequest);
                    List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
                    try {
                        for (String obj : deletedObjects) {
                            String deleteObj = URLDecoder.decode(obj, "UTF-8");
                            System.out.println(deleteObj);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                nextMarker = objectListing.getNextMarker();
            } while (objectListing.isTruncated());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getImgUrl(String filename) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + 1800 * 1000L);
            URL imgUrl = ossClient.generatePresignedUrl(bucketName, filename, expiration);
            return imgUrl.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> listImages(String prefix) {
        try {
            ListObjectsV2Result result = ossClient.listObjectsV2(bucketName, prefix);
            List<OSSObjectSummary> sums = result.getObjectSummaries();
            List<String> filenames = new ArrayList<>();
            for (OSSObjectSummary s : sums) {
                filenames.add(s.getKey());
            }
            return filenames;
        } catch (Exception e) {
            return null;
        }
    }
}
