package com.iecube.community.model.Exam_timing.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置
 */
@Configuration
public class RabbitMQConfig {

    @Value("${timing.task.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${timing.task.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${timing.task.rabbitmq.queue}")
    private String queueName;

    /**
     * 声明交换机
     */
    @Bean
    public DirectExchange examTimingExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    /**
     * 声明队列
     */
    @Bean
    public Queue examTimingFinishQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding binding(Queue examTimingFinishQueue, DirectExchange examTimingExchange) {
        return BindingBuilder.bind(examTimingFinishQueue).to(examTimingExchange).with(routingKey);
    }

}