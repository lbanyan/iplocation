package com.huya.iplocation.log4j2;

import com.github.mydog.common.MapUtil;
import com.github.mydog.common.StringUtil;
import com.github.mydog.common.http.HttpMethod;
import com.github.mydog.common.http.HttpRequest;
import com.github.mydog.common.http.SimpleHttpUtil;
import com.github.mydog.common.json.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * log4j2插件，使用企业微信发送异常信息
 *
 * @author xingping
 * @date 2019-07-17
 */
@Plugin(name = "WeChat", category = "Core", elementType = "appender", printObject = true)
public class WeChatAppender extends AbstractAppender {

    private static final Integer OA_APP_AGENT_ID = 1000035;

    private static final String SEND_MSG_ENDPOINT = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token={}";

    private static final String DMX_ENDPOINT = "https://dmx-jingwei.huya.com/weixin/getToken.do";

    private static final List<String> EMPLOYEES = Arrays.asList("dw_zhangxingping1");

    private static final String PROJECT_NAME = "全球IP位置服务";

    private static final String ENV = System.getProperty("env", System.getenv("env"));

    protected WeChatAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {

        super(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {

        if (!"prod".equals(ENV)) {
            return;
        }

        if ((event.getThrown() != null)
                && (event.getThrown() instanceof IllegalArgumentException)) {
            return;
        }

        try {
            String content;
            byte[] bytes = getLayout().toByteArray(event);
            if (bytes.length > 2048) {
                content = new String(bytes, 0, 2048);
            } else {
                content = new String(bytes);
            }
            send(EMPLOYEES, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void send(List<String> employees, String content) throws Exception {

        StringBuilder touser = new StringBuilder();
        for (String employee : employees) {
            String em[] = StringUtils.split(employee, "_");
            if (em.length == 2) {
                touser.append(em[1]).append("|");
            }
        }

        String token = getToken();
        String url = StringUtil.format(SEND_MSG_ENDPOINT, token);
        Map<String, Object> body = new HashMap<>();
        body.put("touser", touser.substring(0, touser.length() - 1));
        body.put("msgtype", "text");
        body.put("agentid", OA_APP_AGENT_ID);
        body.put("text", MapUtil.as("content", PROJECT_NAME + "\n" + content));
        HttpRequest request = new HttpRequest(url, HttpMethod.POST, null, JsonUtil.writeValueAsString(body));
        SimpleHttpUtil.execute(request);
    }

    /**
     * 获取token
     */
    public static String getToken() throws IOException {

        HttpRequest request = new HttpRequest(DMX_ENDPOINT, HttpMethod.GET);
        return SimpleHttpUtil.execute(request).body;
    }

    @PluginFactory
    public static WeChatAppender createAppender(@PluginAttribute("name") String name,
                                                @PluginElement("Filter") final Filter filter,
                                                @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                @PluginAttribute("ignoreExceptions") boolean ignoreExceptions) {

        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new WeChatAppender(name, filter, layout, ignoreExceptions);
    }
}
