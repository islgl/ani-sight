package cc.lglgl.anisight.utils;

// This file is auto-generated, don't edit it. Thanks.

import com.aliyun.sts20150401.Client;
import com.aliyun.sts20150401.models.AssumeRoleRequest;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Aliyun STS utils
 *
 * @author lgl
 */
@Data
@Component
public class StsUtil {

    /**
     * 使用AK&SK初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "sts.cn-beijing.aliyuncs.com";
        return new Client(config);
    }

    public Map<String, String> getStsCredential(String roleSessionName) throws Exception {
        Client client = StsUtil.createClient(System.getenv("ALIYUN_ACCESS_KEY_ID"),
                System.getenv("ALIYUN_ACCESS_KEY_SECRET"));
        AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest()
                .setRoleArn("acs:ram::1187246145959696:role/anisightoss")
                .setDurationSeconds(900L)
                .setRoleSessionName(roleSessionName);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            AssumeRoleResponse response = client.assumeRoleWithOptions(assumeRoleRequest, runtime);
            AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials credentials = response.body.getCredentials();
            String accessKeyId = credentials.getAccessKeyId();
            String accessKeySecret = credentials.getAccessKeySecret();
            String securityToken = credentials.getSecurityToken();
            return Map.of("accessKeyId", accessKeyId, "accessKeySecret", accessKeySecret, "securityToken", securityToken);
        } catch (TeaException error) {
            System.out.println(error.getMessage());
            System.out.println(error.getData().get("Recommend"));
            Common.assertAsString(error.message);
        } catch (Exception error) {
            TeaException teaError = new TeaException(error.getMessage(), error);
            System.out.println(teaError.getMessage());
            System.out.println(teaError.getData().get("Recommend"));
            Common.assertAsString(teaError.message);
        }
        return null;
    }
}
