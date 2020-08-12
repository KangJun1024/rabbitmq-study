package com.persagy.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

/**
 * @ClassName: Consumer
 * @Description: 消费者
 * @author kangjun
 * @date 2020年3月6日 下午6:39:01
 */
@Component
public class Consumer {


	/**
	 * @Title: acceptSimpleMessage
	 * @Description: 消费简单队列消息
	 * @param message
	 */
	@RabbitListener(queues = "SIMPLE-QUEUE")
    @RabbitHandler
    public void acceptSimpleMessage(String message){
        System.out.println("消费简单队列中的消息："+message);
    }

	/**
	 * @Title: acceptWorkMessageOne
	 * @Description: 工作模式下消費工作队列中的消息，acceptWorkMessageOne和acceptWorkMessageTwo两个队列只能有一个抢到队列中的消息
	 * @param message
	 */
	@RabbitListener(queues = "WORK-QUEUE")
    @RabbitHandler
    public void acceptWorkMessageOne(String message){
        System.out.println("1消费工作队列中的消息："+message);
    }

	@RabbitListener(queues = "WORK-QUEUE")
    @RabbitHandler
    public void acceptWorkMessageTwo(String message){
        System.out.println("2消费工作队列中的消息："+message);
    }

	/**
	 * @Title: acceptDirectMessage
	 * @Description: 监听direct模式队列
	 * @param message
	 * @throws Exception
	 */
	@RabbitListener(queues = "DIRECT-QUEUE")
    @RabbitHandler
    public void acceptDirectMessage(String msg, Channel channel, Message message) throws Exception{
        System.out.println("direct队列中的消息："+msg);
        //Thread.sleep(20000);
        //throw new Exception("123");
        //try {
            //CorrelationData cd = null;
            //cd.getId();
       // } catch (Exception e) {

        //}

    		try {
    			// 消息在channel中的唯一标识
    			Long tag = message.getMessageProperties().getDeliveryTag();
    			System.out.println("----消息的唯一标识----"+tag);

    			 CorrelationData cd = null;
    	         cd.getId();

    			// if (message.getMessageProperties().getDeliveryTag() == 1 || message.getMessageProperties().getDeliveryTag() == 2)
    	          //  {
    	            //    throw new Exception();
    	            //}
    			// 确认收到消息，false只确认当前consumer一个消息收到，true确认所有consumer获得的消息
//    			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    			System.out.println("----确认成功----");
    		} catch (Exception e) {
    			// 是否重复处理失败
    			Boolean isRedeliveredfail = message.getMessageProperties().getRedelivered();
    			System.out.println("----重复处理标识----"+isRedeliveredfail);
    			if (isRedeliveredfail) {//true表示消息已经重复处理失败
    				System.out.println("消息已重复处理失败,拒绝再次接收！");
    				// 拒绝消息，requeue=false 表示不再重新入队，如果配置了死信队列则进入死信队列
    				channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
    			} else {
    				// 如果是第一次失败则再次放入队列
    				System.out.println("消息即将再次返回队列处理！");
    				//Thread.sleep(10000);
    				// requeue为是否重新回到队列，true重新入队
    				channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
    			}
    			//如设置成manual手动确认，一定要对消息做出应答，否则rabbit认为当前队列没有消费完成，将不再继续向该队列发送消息。
    			//channel.basicAck(long,boolean); 确认收到消息，消息将被队列移除，false只确认当前consumer一个消息收到，true确认所有consumer获得的消息。
    			//channel.basicNack(long,boolean,boolean); 确认否定消息，第一个boolean表示一个consumer还是所有，第二个boolean表示requeue是否重新回到队列，true重新入队。
    			//channel.basicReject(long,boolean); 拒绝消息，requeue=false 表示不再重新入队，如果配置了死信队列则进入死信队列。
    			//当消息回滚到消息队列时，这条消息不会回到队列尾部，而是仍是在队列头部，这时消费者会又接收到这条消息，如果想消息进入队尾，须确认消息后再次发送消息。

    			//如设置成manual手动确认,但代码中没有手动确认，则消息会再队列中的unacked中
    			//当没有监听队列时消息在ready中


    		}

    }

	/**
	 * @Title: acceptFanoutOneMessage
	 * @Description: 接受订阅队列的消息
	 * @param message
	 */
	@RabbitListener(queues = "FANOUT-QUEUE-ONE")
    @RabbitHandler
    public void acceptFanoutOneMessage(String message){
        System.out.println("发布订阅的消息1："+message);
    }

	@RabbitListener(queues = "FANOUT-QUEUE-TWO")
    @RabbitHandler
    public void acceptFanoutTwoMessage(String message){
        System.out.println("发布订阅的消息2："+message);
    }

	/**
	 * @Title: acceptDLXMessage
	 * @Description: 监听死信队列
	 * @param message
	 */
	@RabbitListener(queues = "DLX-QUEUE")
    @RabbitHandler
    public void acceptDLXMessage(String message){
        System.out.println("死信队列中的消息："+message);
    }


	@RabbitListener(queues = "DELAYED-QUEUE")
	public void calendarPopUp(byte[] msg) {
		try {
			String s = new String(msg, "UTF-8");
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
