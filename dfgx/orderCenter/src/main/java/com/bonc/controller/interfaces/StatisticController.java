package com.bonc.controller.interfaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.busi.statistic.service.StatisticService;

@RestController
@RequestMapping("/statistic/")
public class StatisticController {

	@Autowired
	private StatisticService statisticService;
	
	
}
