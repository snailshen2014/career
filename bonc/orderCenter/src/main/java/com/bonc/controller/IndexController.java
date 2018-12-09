package com.bonc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bonc.busi.code.service.CodeService;

@Controller
@RequestMapping("")
public class IndexController {
	
	@Autowired
	private CodeService codeService;
	
	@RequestMapping(value="/index")
	public String index(HttpServletRequest request ,HttpServletResponse response){
		return "index";
	}
	
	@RequestMapping(value="/")
	public String home(HttpServletRequest request ,HttpServletResponse response){
		return index(request,response);
	}
	
}
