package com.huya.iplocation.component;

import com.github.mydog.common.IpUtil;
import com.huya.iplocation.sdk.ipip.IpipClient;
import net.ipip.ipdb.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 系统配置初始化
 *
 * @author xingping
 * @date 2019-10-12
 */
@Component
public class AppConfigurationInit implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(AppConfigurationInit.class);

    public static final String INNER_IP;

    public static volatile City CITY_DB;

    @Value("${ipip.download-url}")
    String ipipDownloadUrl;

    @Value("${ipip.path}")
    String ipipPath;

    @Value("${ipip.file-name}")
    String ipipFileName;

    static {

        INNER_IP = IpUtil.getInnerIp();
        System.setProperty("ip", INNER_IP);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        logger.info("init app conf.....");

        String filePath = ipipPath + "/" + ipipFileName;
        File file = new File(filePath);
        if (!file.exists()) {
            String filePathTmp = IpipClient.download(ipipDownloadUrl, ipipPath);
            File fileTmp = new File(filePathTmp);
            String etag = fileTmp.getName().split("\\.")[0];
            IpipClient.forceRename(fileTmp, file);
            IpipClient.etagToFile(etag, ipipPath + "/etag.txt");
        }
        CITY_DB = new City(filePath);
    }
}
