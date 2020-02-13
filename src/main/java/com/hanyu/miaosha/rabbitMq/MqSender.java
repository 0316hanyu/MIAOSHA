package com.hanyu.miaosha.rabbitMq;

import com.hanyu.miaosha.redis.RedisService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class MqSender {
    private static Logger log = LoggerFactory.getLogger(MqSender.class);
    @Autowired
    private AmqpTemplate amqpTemplate ;
    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = RedisService.beanToString(mm);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MqConfig.MIAOSHA_QUEUE, msg);
    }
    public void send(Object message){
        String msg = RedisService.beanToString(message);
		log.info("send message:"+msg);
		amqpTemplate.convertAndSend(MqConfig.QUEUE, msg);
    }
    public void fanoutSend(Object message){
        String msg =RedisService.beanToString(message);
        log.info("send message"+msg);
        amqpTemplate.convertAndSend(MqConfig.FANOUT_EXCHANGE,"",msg);
    }
    public void topicSend(Object message){
        String msg =RedisService.beanToString(message);
        log.info("send message"+msg);
        amqpTemplate.convertAndSend(MqConfig.FANOUT_EXCHANGE,"topic.key1",msg);
    }
}
