package com.eplugger.extend;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.eplugger.utils.json.JSONUtil;

@Component
public class PubFilter implements HandlerInterceptor {
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		//开发模式下测试
		if (Helper.isDev()) {
			Helper.safeDo(() -> {
				//Helper.debug(PubFilter.class,"[url请求路径]"+request.getRequestURI());
				Helper.debug(PubFilter.class,
						"[request参数]\n" + JSONUtil.mapToJson(((HttpServletRequest) request).getParameterMap()));
				Helper.debug(PubFilter.class, "[content-type]" + response.getContentType());
				if (modelAndView != null) {
					Helper.debug(PubFilter.class, String.format("[view path]%s", modelAndView.getViewName()));
				}
			});

		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		Helper.handleException(ex);
	}

}
