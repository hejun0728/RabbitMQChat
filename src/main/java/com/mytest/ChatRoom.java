package com.mytest;

import java.io.IOException;
import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class ChatRoom {
	private final static String EXCHANGE_NAME = "chat-exchange";

	public static void main(String[] args) throws IOException {
		Thread thread = new Thread() {
			ConnectionFactory factory = null;
			Connection connection = null;
			Channel channel = null;
			QueueingConsumer consumer = null;

			public void run() {

				try {
					factory = new ConnectionFactory();
					factory.setHost("localhost");
					connection = factory.newConnection();
					channel = connection.createChannel();

					channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
					// 创建一个非持久的、唯一的且自动删除的队列
					String queueName = channel.queueDeclare().getQueue();
					// 为转发器指定队列，设置binding
					channel.queueBind(queueName, EXCHANGE_NAME, "");

					consumer = new QueueingConsumer(channel);
					channel.basicConsume(queueName, true, consumer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				while (true) {
					QueueingConsumer.Delivery delivery = null;
					try {
						delivery = consumer.nextDelivery();
					} catch (ShutdownSignalException e) {

						e.printStackTrace();
					} catch (ConsumerCancelledException e) {

						e.printStackTrace();
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					String message = new String(delivery.getBody());

					if ("q".equals(message)) {
						try {
							channel.close();
							connection.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return;
					}
					System.out.println(message);

				}
			}
		};
		thread.start();

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		// 声明转发器和类型
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		Scanner scan = new Scanner(System.in);

		System.out.println("Welcome to Rabbit ChatRoom .^-^.");
		System.out.println("Type q to exit ...");
		System.out.println("Input your nickname first .^-^.");
		String name = scan.next();
		System.out.println("Hello  " + name + " , you can chat from now,enjoy it");
		while (true) {
			String message = scan.next();
			if ("q".equals(message)) {
				channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
				System.out.println("GoodBye .^-^.");
				channel.close();
				connection.close();
				return;
			}
			channel.basicPublish(EXCHANGE_NAME, "", null, (name + " said " + message).getBytes());

		}

	}
}
