package com.hanyu.miaosha.controller;

import com.hanyu.miaosha.domain.User;
import com.hanyu.miaosha.rabbitMq.MqSender;
import com.hanyu.miaosha.redis.RedisService;
import com.hanyu.miaosha.redis.UserKey;
import com.hanyu.miaosha.result.CodeMsg;
import com.hanyu.miaosha.result.Result;
import com.hanyu.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/demo")
public class DemoController {
		@Autowired
		private UserService userService;
		@Autowired
		private RedisService redisService;
		@Autowired
		private MqSender sender;
	 	@RequestMapping("/")
	    @ResponseBody
	    String home() {
	 		return "Hello World!";
	    }
	 	//1.rest api json输出 2.页面
	 	@RequestMapping("/hello")
	    @ResponseBody
	    public Result<String> hello() {
	 		return Result.success("hello,imooc");
	       // return new Result(0, "success", "hello,imooc");
	    }
	 	
	 	@RequestMapping("/helloError")
	    @ResponseBody
	    public Result<String> helloError() {
	 		return Result.error(CodeMsg.SERVER_ERROR);
	 		//return new Result(500102, "XXX");
	    }
	 	
	 	@RequestMapping("/thymeleaf")
	    public String  thymeleaf(Model model) {
	 		model.addAttribute("name", "hanyu");
	 		return "hello";
	    }

	    @RequestMapping("/db/get")
		@ResponseBody
		public Result<User> dbGet(){
	 		User user=userService.getById(1);
	 		return Result.success(user);
		}
		@RequestMapping("/db/tx")
		@ResponseBody
		public Result<Boolean> dbTx() {
			boolean b=userService.tx();
			return Result.success(b);
		}
		@RequestMapping("/redis/get")
		@ResponseBody
		public Result<User> redisGet() {
			User  user  = redisService.get(UserKey.getById, ""+1, User.class);
			return Result.success(user);
		}

		@RequestMapping("/redis/set")
		@ResponseBody
		public Result<Boolean> redisSet() {
			User user  = new User();
			user.setId(1);
			user.setName("1111");
			redisService.set(UserKey.getById, ""+1, user);//UserKey:id1
			return Result.success(true);
	}
	@RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
		sender.send("hello,hanyu");
        return Result.success("Hello，hanyu");
    }
	@RequestMapping("/fanout")
	@ResponseBody
	public Result<String> fanout() {
		sender.fanoutSend("hello,hanyu");
		return Result.success("Hello，hanyu");
	}
	@RequestMapping("/topic")
	@ResponseBody
	public Result<String> topic() {
		sender.topicSend("hello,hanyu");
		return Result.success("Hello，hanyu");
	}
}
