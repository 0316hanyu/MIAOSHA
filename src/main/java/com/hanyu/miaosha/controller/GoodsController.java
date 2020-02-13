package com.hanyu.miaosha.controller;

import com.hanyu.miaosha.domain.MiaoshaUser;
import com.hanyu.miaosha.redis.GoodsKey;
import com.hanyu.miaosha.redis.RedisService;
import com.hanyu.miaosha.result.Result;
import com.hanyu.miaosha.service.GoodsService;
import com.hanyu.miaosha.service.MiaoshaUserService;
import com.hanyu.miaosha.vo.GoodsDetailVo;
import com.hanyu.miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	private GoodsService goodsService;

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;

	@Autowired
	ApplicationContext applicationContext;
	//5000次 QPS:255
	//缓存597
	@RequestMapping(value = "/to_list",produces = "text/html")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
		model.addAttribute("user", user);
		//取缓存
		String html=redisService.get(GoodsKey.getGoodsList,"",String.class);
		if(!StringUtils.isEmpty(html)){
			return html;
		}
		//查询商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList",goodsList);
		//return "goods_list";
		SpringWebContext ctx=new SpringWebContext(request,response,
				request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
		html=thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
		if(!StringUtils.isEmpty(html)){
			redisService.set(GoodsKey.getGoodsList, "", html);
		}
		return html;
	}
	@RequestMapping(value="/to_detail2/{goodsId}",produces="text/html")
	@ResponseBody
	public String detail2(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user,
						  @PathVariable("goodsId")long goodsId) {
		model.addAttribute("user", user);

		//取缓存
		String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
		if(!StringUtils.isEmpty(html)) {
			return html;
		}
		//手动渲染
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods", goods);

		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if(now < startAt ) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int)((startAt - now )/1000);
		}else  if(now > endAt){//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		model.addAttribute("miaoshaStatus", miaoshaStatus);
		model.addAttribute("remainSeconds", remainSeconds);
//        return "goods_detail";

		SpringWebContext ctx = new SpringWebContext(request,response,
				request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
		html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
		if(!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
		}
		return html;
	}
	@RequestMapping("/detail/{goodsId}")
	@ResponseBody
	public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
										@PathVariable("goodsId")long goodsId) {
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();
		int miaoshaStatus = 0;
		int remainSeconds = 0;
		if(now < startAt ) {//秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int)((startAt - now )/1000);
		}else  if(now > endAt){//秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else {//秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		GoodsDetailVo vo = new GoodsDetailVo();
		vo.setGoods(goods);
		vo.setUser(user);
		vo.setRemainSeconds(remainSeconds);
		vo.setMiaoshaStatus(miaoshaStatus);
		return Result.success(vo);
	}
}
