package com.persagy.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

	@Bean
	public Queue simpleQueue () {
		return new Queue ("SIMPLE-QUEUE");
	}

	@Bean
	public Queue workQueue () {
		return new Queue ("WORK-QUEUE");
	}


	/**
	 * @Title: fanoutQueueOne
	 * @Description: 发布订阅模式
	 * @return
	 */
	@Bean
	public Queue fanoutQueueOne () {
		return new Queue ("FANOUT-QUEUE-ONE");
	}

	@Bean
	public Queue fanoutQueueTwo () {
		return new Queue ("FANOUT-QUEUE-TWO");
	}

	@Bean
	public FanoutExchange fanoutExchange () {
		return new FanoutExchange ("FANOUT-EXCHANGE");
	}

	@Bean
	public Binding bindingFanoutQueueOneToFanoutExchange() {
		return BindingBuilder.bind(fanoutQueueOne()).to(fanoutExchange());
	}

	@Bean
	public Binding bindingFanoutQueueTwoToFanoutExchange() {
		return BindingBuilder.bind(fanoutQueueTwo()).to(fanoutExchange());
	}



	/**注：DLX((Dead Letter Exchanges))是专门用来存储死信消息到指定队列中的一种交换机。需要在声明队列时指定DLX和死信存放队列的路由Key，因为RabbitMQ是通过Exchange去匹配路由key寻找队列的。
	 * 死信消息产生的条件
	 * 1、消息被拒(basic.reject or basic.nack)并且没有重新入队(requeue=false)；
	 * 2、消息在队列中过期，即当前消息在队列中的存活时间已经超过了预先设置的TTL(Time To Live)时间；
	 * 3、当前队列中的消息数量已经超过最大长度。
	 * 消息进入死信队列的过程：消息 -> 队列 （触发以上条件）-> DLX交换机 -> DLK队列
	 *
	 *
	 *
	 *
	 * @Title: DLXQueue
	 * @Description: 死信队列
	 * @return
	 */
	@Bean
	public Queue DLXQueue () {
		return new Queue ("DLX-QUEUE");
	}

	@Bean
	public DirectExchange DLXExchange () {
		return new DirectExchange ("DLX-EXCHANGE");
	}

	@Bean
	public Binding bindingDLXQueueToDLXExchange() {
		return BindingBuilder.bind(DLXQueue()).to(DLXExchange()).with("DLX-BINGD-KEY");
	}

	/**
	 * @Title: commonQueue
	 * @Description: 普通队列
	 * @return
	 */
	@Bean
	public Queue commonQueue () {
		 // x-dead-letter-exchange 声明了队列里的死信转发到的DLX名称，
		 // x-dead-letter-routing-key 声明了这些死信在转发时携带的 routing-key 名称。

		return QueueBuilder.durable("COMMON-QUEUE").withArgument("x-dead-letter-exchange", "DLX-EXCHANGE")
				.withArgument("x-dead-letter-routing-key", "DLX-BINGD-KEY")
				// .withArgument("x-message-ttl", QUEUE_EXPIRATION) // 设置队列的过期时间
				.build();
	}

	@Bean
	public DirectExchange commonExchange () {
		return new DirectExchange ("COMMON-EXCHANGE");
	}

	@Bean
	public Binding bindingCommonQueueToCommonExchange() {
		return BindingBuilder.bind(commonQueue()).to(commonExchange()).with("COMMON-BINGD-KEY");
	}

	@Bean
	public Queue directQueue () {
		return QueueBuilder.durable("DIRECT-QUEUE").withArgument("x-dead-letter-exchange", "DLX-EXCHANGE")
				.withArgument("x-dead-letter-routing-key", "DLX-BINGD-KEY")
				// .withArgument("x-message-ttl", QUEUE_EXPIRATION) // 设置队列的过期时间
				.build();
	}

	@Bean
	public DirectExchange directExchange () {
		return new DirectExchange ("DIRECT-EXCHANGE");
	}


	/**
	 * @Title: bindingDirectQueueToDirectExchange
	 * @Description: 绑定交换机和队列
	 * 注意：绑定key和生产者发送消息时的路由key一致,否则Exchange不知道消息发送到哪个队列。最大长度255 字节
	 * @return
	 */
	@Bean
	public Binding bindingDirectQueueToDirectExchange() {
		return BindingBuilder.bind(directQueue()).to(directExchange()).with("DIR-BINGD-KEY");
	}

}
