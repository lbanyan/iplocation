package com.huya.iplocation.sdk.ipip;

import com.github.mydog.common.http.IgnoreHttps;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IpipClient {

    private static final String HTTP_METHOD = "GET";
    private static final int HTTP_CONNECTION_TIMEOUT = 5000;
    private static final int HTTP_READ_TIMEOUT = 10 * 60 * 1000;

    /**
     * 下载IPIP文件
     *
     * @param url  下载地址
     * @param path 文件保存路径
     * @return 最终文件完整路径
     * @throws Exception
     */
    public static String download(String url, String path) throws IOException, NoSuchAlgorithmException {

        if (url == null || path == null) {
            throw new IllegalArgumentException("参数url、path均不可为空");
        }
        HttpURLConnection connection = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            // 获取连接
            connection = createConnection(url);
            connection.setRequestMethod(HTTP_METHOD);
            connection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
            connection.setReadTimeout(HTTP_READ_TIMEOUT);
            connection.connect();
            int code = connection.getResponseCode();
            String message = connection.getResponseMessage();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("IPIP下载请求失败，http code = " + code + ", http message = " + message);
            }
            String etag = connection.getHeaderField("ETag");
            if (etag == null || etag.length() == 0) {
                throw new RuntimeException("IPIP下载请求失败，ETag不存在");
            }
            in = connection.getInputStream();
            String filePath = path + "/" + etag + ".ipdb";
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            out = new FileOutputStream(filePath);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            String etag1 = "sha1-" + sha1(file);
            if (!etag.equals(etag1)) {
                throw new RuntimeException("IPIP下载文件不完整");
            }
            return filePath;
        } finally {
            close(connection, in, out);
        }
    }

    public static HttpURLConnection createConnection(String url) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        if ("https".equalsIgnoreCase(url.substring(0, 5))) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(IgnoreHttps.getSslSocketFactory());
            ((HttpsURLConnection) conn).setHostnameVerifier(IgnoreHttps.getHostnameVerifier());
        }
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Connection", "close");
        return conn;
    }

    public static void close(HttpURLConnection connection, InputStream in, FileOutputStream out) {

        if (connection != null) {
            connection.disconnect();
        }
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
    }

    /**
     * SHA1 文件完整性验证算法
     *
     * @param file 文件
     * @return
     * @throws Exception
     */
    public static String sha1(File file) throws NoSuchAlgorithmException, IOException {

        InputStream in = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            in = new FileInputStream(file);
            int len = 0;
            byte[] buffer = new byte[8192];
            while (len != -1) {
                len = in.read(buffer);
                if (len > 0) {
                    digest.update(buffer, 0, len);
                }
            }
            return Hex.encodeHexString(digest.digest());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * 文件强制重命名
     *
     * @param source 源文件
     * @param target 目标文件
     */
    public static void forceRename(File source, File target) {

        if (target.exists()) {
            target.delete();
        }
        source.renameTo(target);
    }

    /**
     * 将etag记入文件
     *
     * @param etag
     * @param filePath
     * @throws Exception
     */
    public static void etagToFile(String etag, String filePath) throws IOException {

        File file = new File(filePath);
        file.createNewFile();
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));
            out.write(etag);
            out.flush();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}
