package com.hanyu.miaosha.rabbitMq;


import com.hanyu.miaosha.domain.MiaoshaOrder;
import com.hanyu.miaosha.domain.MiaoshaUser;
import com.hanyu.miaosha.redis.RedisService;
import com.hanyu.miaosha.service.GoodsService;
import com.hanyu.miaosha.service.MiaoshaService;
import com.hanyu.miaosha.service.OrderService;
import com.hanyu.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqReceiver {
    private static Logger log= LoggerFactory.getLogger(MqReceiver.class);
    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues=MqConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        log.info("receive message:"+message);
        MiaoshaMessage mm  = RedisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);
    }
    /*@RabbitListener(queues=MqConfig.QUEUE)
    public void receive(String message) {
        log.info("receive message:"+message);
    }
    @RabbitListener(queues=MqConfig.QUEUE1)
    public void receive1(String message) {
        log.info("receive queue1 message:"+message);
    }
    @RabbitListener(queues=MqConfig.QUEUE2)
    public void receive2(String message) {
        log.info("receive queue2 message:"+message);
    }*/
}
