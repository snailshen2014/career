package com.bonc.controller.interfaces;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.busi.interfaces.service.TalkService;

@RestController
@RequestMapping("/talk/")
public class TalkController {

	@Autowired
	private TalkService talkService;
	
	@RequestMapping(value="variablereplace")
	public Object variablereplace(@RequestBody HashMap<String, String> req){
		return talkService.exchangeTalkVal(req);
	}
	
	@RequestMapping(value="variablereplacelist")
	public Object variablereplacelist(@RequestBody List<HashMap<String, String>> req){
		return talkService.exchangeTalkVals(req);
	}
	
}
