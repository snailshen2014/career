package com.bonc.common.utils;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

/**
 * Created by jeff on 15/10/23.
 */
@Order(1)
@ControllerAdvice(basePackages = "com.bonc.controller.interfaces")
public class MyRequestBodyAdvice implements RequestBodyAdvice {
	private static final Logger log = Logger.getLogger(MyRequestBodyAdvice.class);
	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage,
			MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage,
			MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType)
			throws IOException {
//		log.info("請求參數！");
		return inputMessage;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
			MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
//		log.info("請求參數！");
		return body;
	}

}