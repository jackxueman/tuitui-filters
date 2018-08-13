package com.tuitui.filter.interceptor;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @date 2017-5-27
 * @version V1.0
 */
public class LoginInterceptor implements HandlerInterceptor {
	Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		// 是否要处理
		String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);

		String username = (String) request.getSession().getAttribute("username");
		if (username == null) {
			logger.info("用户没有登陆！  uri:{}", lookupPath);
			Map<String, Object> notlogin = new HashMap<>();
			notlogin.put("errno", 401);
			notlogin.put("info", "please login first");
			response.getWriter().write(JSONObject.toJSONString(notlogin));
			response.getWriter().flush();
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}