package com.huya.iplocation.daemon;

import com.huya.iplocation.component.AppConfigurationInit;
import com.huya.iplocation.sdk.ipip.IpipClient;
import net.ipip.ipdb.City;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * IPIP离线库下载
 *
 * @author xingping
 * @date 2019-10-12
 */
@Component
public class IpipDownloadDaemon {

    @Value("${ipip.download-url}")
    String ipipDownloadUrl;

    @Value("${ipip.path}")
    String ipipPath;

    @Value("${ipip.file-name}")
    String ipipFileName;

    @Scheduled(cron = "0 5 4 * * *")
    public void execute() throws Exception {

        String filePathTmp = IpipClient.download(ipipDownloadUrl, ipipPath);
        File fileTmp = new File(filePathTmp);
        String etag = fileTmp.getName().split("\\.")[0];
        String filePath = ipipPath + "/" + ipipFileName;
        File file = new File(filePath);
        IpipClient.forceRename(fileTmp, file);
        IpipClient.etagToFile(etag, ipipPath + "/etag.txt");
        AppConfigurationInit.CITY_DB = new City(filePath);
    }
}
