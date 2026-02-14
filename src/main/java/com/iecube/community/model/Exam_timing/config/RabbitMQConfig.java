package com.iecube.community.model.Exam_timing.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置
 */
@Configuration
@Slf4j
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
        // 声明直连交换机，持久化
        return ExchangeBuilder.directExchange(exchangeName)
                .durable(true)
                .build();
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
    public Binding bindingExamTimingFinish(Queue examTimingFinishQueue, Exchange examTimingExchange) {
        return BindingBuilder.bind(examTimingFinishQueue)
                .to(examTimingExchange)
                .with(routingKey)
                .noargs();
    }


    // ❶ 核心：配置 JSON 消息转换器（替代 Java 序列化）
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ❷ 消费者容器工厂（关联 JSON 转换器）
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 关键：使用 JSON 转换器
        factory.setMessageConverter(jackson2JsonMessageConverter());
        // 可选：消费者参数配置
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(5);
        factory.setDefaultRequeueRejected(false); // 失败不重入队
        return factory;
    }

    // ❸ 生产者 RabbitTemplate（关联 JSON 转换器，确保发送的是 JSON 消息）
    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate = new org.springframework.amqp.rabbit.core.RabbitTemplate(connectionFactory);
        // 关键：生产者也用 JSON 转换器
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

}