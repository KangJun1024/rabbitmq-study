package com.persagy;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.persagy.productor.Producer;

@SpringBootTest
public class RabbitmqStudyApplicationTests {

	@Autowired
	public Producer producer;

	@Test
	public void contextLoads() {
		producer.directDemo();
	}

}
