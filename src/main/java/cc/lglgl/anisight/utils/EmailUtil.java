package cc.lglgl.anisight.utils;

import com.aliyun.dm20151123.Client;
import com.aliyun.dm20151123.models.SingleSendMailRequest;
import com.aliyun.dm20151123.models.SingleSendMailResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;

/**
 * @author lgl
 */
public class EmailUtil {
    private static Client createClient() throws Exception {
        Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(System.getenv("OSS_ACCESS_KEY_ID"))
                .setAccessKeySecret(System.getenv("OSS_ACCESS_KEY_SECRET"));
        config.endpoint = "dm.aliyuncs.com";
        return new Client(config);
    }

    /**
     * 发送邮件
     * @param account 0: noreply 1: service
     * @param toAddress 收件地址
     * @param subject 主题
     * @param textBody 正文
     * @return 事件ID与请求ID
     */
    public static SingleSendMailResponse sendEmail(
            int account,
            String toAddress,
            String subject,
            String textBody
    ) {
        try {
            String accountName = account == 0 ? "noreply@lewisliugl.cn" : "service@lewisliugl.cn";
            Client client = EmailUtil.createClient();
            SingleSendMailRequest singleSendMailRequest = new SingleSendMailRequest()
                    .setAccountName(accountName)
                    .setAddressType(1)
                    .setToAddress(toAddress)
                    .setSubject(subject)
                    .setTextBody(textBody)
                    .setFromAlias("AniSight")
                    .setReplyToAddress(false);
            RuntimeOptions runtime = new RuntimeOptions();
            return client.singleSendMailWithOptions(singleSendMailRequest, runtime);
        } catch (TeaException error) {
            System.out.println(error.getMessage());
            System.out.println(error.getData().get("Recommend"));
            Common.assertAsString(error.message);
        } catch (Exception e) {
            TeaException error = new TeaException(e.getMessage(), e);
            System.out.println(error.getMessage());
            System.out.println(error.getData().get("Recommend"));
            Common.assertAsString(error.message);
        }
        return null;
    }
}
