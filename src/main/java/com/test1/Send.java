package com.test1;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {
	private final static String QUEUE_NAME = "hello";

	public static void main(String[] args) throws IOException {
   /**
    * 连接到RabbitMQ
    */
		ConnectionFactory factory = new ConnectionFactory();
		 //设置MabbitMQ所在主机ip或者主机名  
		factory.setHost("localhost");
		// 创建连接
		Connection connection = factory.newConnection();
		// 创建频道
		Channel channel = connection.createChannel();
		// 指定队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		// 发送消息
	   String message = "Hello ,My  name  is  DD";
		// 往队列发送消息
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
		System.out.println("我发送的消息是：" + message);
		// 关闭频道和连接
		channel.close();
		connection.close();

	}

}