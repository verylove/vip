package com.yzc.vip.demo.mvc.action;


import com.yzc.vip.demo.service.itf.IDemoService;
import com.yzc.vip.spring.annotation.Autowried;
import com.yzc.vip.spring.annotation.Controller;
import com.yzc.vip.spring.annotation.RequestMapping;

@Controller
public class MyAction {

		@Autowried
		IDemoService demoService;

		@RequestMapping("/index.html")
		public void query(){

		}

}
