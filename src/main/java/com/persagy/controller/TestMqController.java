package com.persagy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.persagy.productor.Producer;

@RestController
public class TestMqController {

	@Autowired
	public Producer producer;

	@RequestMapping("/test")
	public void test () {
		producer.simpleDemo();
		producer.workDemo();
		producer.directDemo();
		producer.sendDLXDemo();
		producer.fanoutDemo();

		producer.sendToDeadQueue(1000);
	}

	@RequestMapping("/test2")
	public void test2 () {
//		producer.simpleDemo();
//		producer.workDemo();
//		producer.directDemo();
//		producer.sendDLXDemo();
//		producer.fanoutDemo();

		producer.sendToDeadQueue(8);
	}


}
