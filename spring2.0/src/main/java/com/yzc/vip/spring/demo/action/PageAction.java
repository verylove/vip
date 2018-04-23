package com.yzc.vip.spring.demo.action;

import java.util.HashMap;
import java.util.Map;

import com.yzc.vip.spring.demo.service.IQueryService;
import com.yzc.vip.spring.formework.annotation.GPAutowired;
import com.yzc.vip.spring.formework.annotation.GPController;
import com.yzc.vip.spring.formework.annotation.GPRequestMapping;
import com.yzc.vip.spring.formework.annotation.GPRequestParam;
import com.yzc.vip.spring.formework.webmvc.GPModelAndView;

/**
 * 公布接口url
 * @author Tom
 *
 */
@GPController
@GPRequestMapping("/")
public class PageAction {

	@GPAutowired
    IQueryService queryService;

	@GPRequestMapping("/first.html")
	public GPModelAndView query(@GPRequestParam("teacher") String teacher){
		String result = queryService.query(teacher);
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("teacher", teacher);
		model.put("data", result);
		model.put("token", "123456");
		return new GPModelAndView("first.html",model);
	}

}
