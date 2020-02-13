package com.hanyu.miaosha.rabbitMq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {
    public static final String QUEUE = "queue";
    public static final String QUEUE1 = "queue1";
    public static final String QUEUE2 = "queue2";
    public static final String MIAOSHA_QUEUE = "miaosha.queue";
    public static final String TOPIC_EXCHANGE = "topicExchage";
    public static final String FANOUT_EXCHANGE = "fanoutxchage";
    //简单模式
    @Bean
    public Queue queue(){
        return  new Queue(QUEUE,true);
    }
    @Bean
    public Queue queue1(){
        return  new Queue(QUEUE1,true);
    }
    @Bean
    public Queue queue2(){
        return  new Queue(QUEUE2,true);
    }
    @Bean
    public Queue miaoshaQueue(){
        return new Queue(MIAOSHA_QUEUE,true);
    }
    //发布订阅模式Fanout
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding FanoutBinding1(){
        return BindingBuilder.bind(queue1()).to(fanoutExchange());
    }
    @Bean
    public Binding FanoutBinding2(){
        return BindingBuilder.bind(queue2()).to(fanoutExchange());
    }
    //topic模式
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(queue1()).to(topicExchange()).with("topic.key1");
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(queue2()).to(topicExchange()).with("topic.#");
    }
}
