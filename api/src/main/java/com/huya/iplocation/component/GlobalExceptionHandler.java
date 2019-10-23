package com.huya.iplocation.component;

import com.github.mydog.common.json.JsonUtil;
import com.huya.iplocation.common.HttpResult;
import net.ipip.ipdb.IPFormatException;
import net.ipip.ipdb.NotFoundException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * controller全局统一异常处理
 *
 * @author zhuhui
 */
@Component
@Order(-500)
public class GlobalExceptionHandler implements HandlerExceptionResolver {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final ModelAndView EMPTY_MODEL_AND_VIEW = new ModelAndView();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        return resolveException(response, ex);
    }

    public static ModelAndView resolveException(HttpServletResponse response, Exception ex) {

        if (!(ex instanceof IllegalArgumentException)
                && !(ex instanceof HttpRequestMethodNotSupportedException)
                && !(ex instanceof IPFormatException)
                && !(ex instanceof NotFoundException)) {
            logger.error(ex.getMessage(), ex);
        }
        String message = ex.getMessage();
        int status = 0;
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            HttpResult HttpResult = new HttpResult();
            HttpResult.status = status;
            HttpResult.message = message;
            writer.write(JsonUtil.writeValueAsString(HttpResult));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return EMPTY_MODEL_AND_VIEW;
    }
}
