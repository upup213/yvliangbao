package com.yvliangbao.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SETTLEMENT_QUEUE = "queue.settlement";
    public static final String SETTLEMENT_EXCHANGE = "exchange.settlement";
    public static final String SETTLEMENT_ROUTING_KEY = "settlement.create";

    @Bean
    public Queue settlementQueue() {
        return QueueBuilder.durable(SETTLEMENT_QUEUE)
                .withArgument("x-dead-letter-exchange", SETTLEMENT_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", SETTLEMENT_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public DirectExchange settlementExchange() {
        return new DirectExchange(SETTLEMENT_EXCHANGE);
    }

    @Bean
    public Binding settlementBinding(Queue settlementQueue, DirectExchange settlementExchange) {
        return BindingBuilder.bind(settlementQueue)
                .to(settlementExchange)
                .with(SETTLEMENT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
