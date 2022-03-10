package com.alinote.api.config;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class GetMethodConvertingFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		chain.doFilter(wrapRequest((HttpServletRequest) request), response);
	}

	@Override
	public void destroy() {

	}

	private static HttpServletRequestWrapper wrapRequest(HttpServletRequest request) {
		return new HttpServletRequestWrapper(request) {
			@Override
			public String getMethod() {
				return "GET";
			}
		};
	}
}