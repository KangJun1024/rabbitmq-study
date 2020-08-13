package com.persagy.productor;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: Productor
 * @Description: 生产者
 * @author kangjun
 * @date 下午6:44:26
 */
@Component
public class Producer {

	@Autowired
	public RabbitTemplate rabbitTemplate;

	/**
	 * @Title: simpleDemo
	 * @Description: 简单模式
	 */
	public void simpleDemo () {
		System.out.println("--简单模式--");
		rabbitTemplate.convertAndSend("SIMPLE-QUEUE", "simple");

	}

	/**
	 * @Title: workDemo
	 * @Description: 工作模式
	 */
	public void workDemo () {
		System.out.println("--工作模式--");
		rabbitTemplate.convertAndSend("WORK-QUEUE", "work");

	}

	/**
	 * @Title: directDemo
	 * @Description: 直接匹配
	 */
	public void directDemo () {
		System.out.println("--direct模式--");
		CorrelationData cd = new CorrelationData();
		cd.setId("1slq");
		rabbitTemplate.convertAndSend("DIRECT-EXCHANGE", "DIR-BINGD-KEY", "direct", cd);

	}

	public void fanoutDemo () {
		System.out.println("--fanout模式--");
		CorrelationData cd = new CorrelationData();
		cd.setId("1slq");
		rabbitTemplate.convertAndSend("FANOUT-EXCHANGE", null, "fanout", cd);

	}

	/**
	 * @Title: sendDLXDemo
	 * @Description: 过期进入死信队列
	 */
	public void sendDLXDemo() {
		CorrelationData cd = new CorrelationData();
		cd.setId("1slq");
		rabbitTemplate.convertAndSend("COMMON-EXCHANGE", "COMMON-BINGD-KEY", "死信信息", new MessagePostProcessor() {

			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				message.getMessageProperties().setExpiration("20000");//ms setHeader("x-delay", 30000);
				// 设置消息持久化到磁盘（默认是持久化）。
				// message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
				return message;
			}
		}, cd);
		System.out.println("---死信和存活时长!---");
	}

	public void sendToDeadQueue(Integer expire) {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setDelay(expire * 1000);
		messageProperties.setContentType("json");
		Message message = new Message(expire.toString().getBytes(), messageProperties);
		rabbitTemplate.convertAndSend("55", "77", message);
	}

}
