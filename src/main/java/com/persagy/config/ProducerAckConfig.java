package com.persagy.config;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ProducerAckConfig
 * @Description: 生产者消息投递情况，确保消息可靠投递。请参考：https://www.cnblogs.com/wangiqngpei557/p/9381478.html
 * @author kangjun
 * @date 2020年3月7日 下午6:15:48
 */
@Component
public class ProducerAckConfig implements ConfirmCallback, ReturnCallback {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@PostConstruct
	public void init() {
		rabbitTemplate.setConfirmCallback(this); // 指定 ConfirmCallback 否则确认不起作用
		rabbitTemplate.setReturnCallback(this); // 指定 ReturnCallback

	}

	/**
	 * message 从 producer 到 rabbitmq broker cluster 则会返回一个 confirmCallback，已实现方法confirm中ack属性为标准，true到达，反之进入黑洞。
	 * 注：重点是 CorrelationData 对象，每个发送的消息都需要配备一个 CorrelationData 相关数据对象，CorrelationData 对象内部只有一个 id 属性，用来表示当前消息唯一性。发送的时候创建一个 CorrelationData对象
	 * 注：如果是 cluster 模式，需要所有 broker 接收到才会调用 confirmCallback
	 */
	@Override
	public void confirm(CorrelationData arg0, boolean ack, String arg2) {
		if (ack) {
			System.out.println(arg2+"消息发送成功" + arg0);
		} else {
			System.out.println(arg2+"消息发送失败:" + arg0);
		}

	}

	/**
	 * message 从 exchange->queue 投递失败则会返回一个 returnCallback
	 * confirmCallback方法只是标示 broker 接收到只能表示 message 已经到达服务器，并不能保证消息一定会被投递到目标 queue 里。所以需要用到接下来的 returnCallback 。
	 */
	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		// 反序列化对象输出
		System.out.println("消息主体: " + message.toString());
		System.out.println("应答码: " + replyCode);
		System.out.println("描述：" + replyText);
		System.out.println("消息使用的交换器 exchange : " + exchange);
		System.out.println("消息使用的路由键 routing : " + routingKey);

	}

}
