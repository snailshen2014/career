package com.bonc.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;

@Order(1)
@WebFilter(filterName = "backFilter", urlPatterns = "/back/*")
public class BackFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		if (req.getSession().getAttribute("tenantId") == null && !req.getRequestURL().toString().contains("register")
				&& !req.getRequestURL().toString().contains("main")
				&& !req.getRequestURL().toString().contains("getTenantId")) {
			res.sendRedirect(req.getContextPath() + "/back/main");
		}
		filterChain.doFilter(req, res);
	}

	@Override
	public void destroy() {
	}
}
