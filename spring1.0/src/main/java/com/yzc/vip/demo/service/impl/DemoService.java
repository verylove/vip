package com.yzc.vip.demo.service.impl;


import com.yzc.vip.demo.service.itf.IDemoService;
import com.yzc.vip.spring.annotation.Service;

@Service
public class DemoService implements IDemoService {

	@Override
	public String get(String name) {
		return "My name is " + name;
	}

}
