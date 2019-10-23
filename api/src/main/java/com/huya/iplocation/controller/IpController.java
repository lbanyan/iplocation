package com.huya.iplocation.controller;

import com.github.mydog.common.AssertUtil;
import com.github.mydog.common.EmptyUtil;
import com.huya.iplocation.common.HttpResult;
import com.huya.iplocation.component.AppConfigurationInit;
import com.huya.iplocation.model.Ip;
import com.huya.iplocation.sdk.ipip.IpipClient;
import net.ipip.ipdb.CityInfo;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * IP查询服务
 *
 * @author xingping
 * @date 2019-10-12
 */
@Controller
@RequestMapping(value = "/ip")
public class IpController {

    private final Object lock = new Object();

    @Value("${ipip.path}")
    String ipipPath;

    @Value("${ipip.file-name}")
    String ipipFileName;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public HttpResult<Ip> get(String ip) throws Exception {

        AssertUtil.notEmpty(ip, "ip is null");

        CityInfo info = AppConfigurationInit.CITY_DB.findInfo(ip, "CN");
        Ip result = new Ip();
        result.country = info.getCountryName();
        result.province = info.getRegionName();
        result.city = info.getCityName();
        result.isp = info.getIspDomain();
        result.ip = ip;
        return new HttpResult<>(result);
    }

    /**
     * IPIP库下载，极端情况下（文件定时更新时）会出现etag和文件不匹配的问题
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/ipip/download", method = RequestMethod.GET)
    @ResponseBody
    public void ipipDownload(HttpServletResponse response) throws Exception {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=ipip.ipdb");
        String ipipPath = this.ipipPath + "/" + ipipFileName;
        File ipipFile = new File(ipipPath);
        String etagPath = this.ipipPath + "/etag.txt";
        File etagFile = new File(etagPath);
        String etag = null;
        if (!etagFile.exists()) {
            synchronized (lock) {
                if (!etagFile.exists()) {
                    etag = "sha1-" + IpipClient.sha1(ipipFile);
                    IpipClient.etagToFile(etag, etagPath);
                }
            }
        }
        if (EmptyUtil.isEmpty(etag)) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(etagPath)));
                etag = reader.readLine();
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
        response.setHeader("ETag", etag);

        ServletOutputStream out = null;
        FileInputStream in = null;
        try {
            out = response.getOutputStream();
            in = new FileInputStream(ipipPath);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
}