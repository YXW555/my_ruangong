package com.second.hand.trading.server.controller;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.second.hand.trading.server.dto.ResultVo;
import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.entity.OrderModel;
import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "/alipay")
public class AliPayController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private IdleItemService idleItemService;

	/**
	 * 模拟支付接口（不调用真实支付宝）
	 * @param orderId 订单ID
	 * @return 支付结果
	 */
	@PostMapping("/simulatePay")
	@SuppressWarnings("unchecked")
	public ResultVo<OrderModel> simulatePay(@CookieValue("shUserId") String shUserId,
	                            @RequestParam Long orderId) {
		try {
			System.out.println("========== 开始模拟支付 ==========");
			System.out.println("订单ID: " + orderId);
			System.out.println("用户ID: " + shUserId);

			// 获取订单信息
			OrderModel order = orderService.getOrder(orderId);
			if (order == null) {
				ResultVo<OrderModel> resultVo = ResultVo.fail(ErrorMsg.PARAM_ERROR);
				resultVo.setMsg("订单不存在");
				return resultVo;
			}

			// 验证订单是否属于当前用户
			if (!order.getUserId().equals(Long.valueOf(shUserId))) {
				ResultVo<OrderModel> resultVo = ResultVo.fail(ErrorMsg.PARAM_ERROR);
				resultVo.setMsg("无权操作此订单");
				return resultVo;
			}

			// 验证订单状态（只有待付款订单才能支付）
			if (order.getOrderStatus() != 0) {
				ResultVo<OrderModel> resultVo = ResultVo.fail(ErrorMsg.PARAM_ERROR);
				resultVo.setMsg("订单状态不正确，无法支付");
				return resultVo;
			}

			// 更新订单状态为已支付
			order.setPaymentStatus((byte) 1); // 已支付
			order.setPaymentTime(new Date());
			order.setOrderStatus((byte) 1); // 待发货

			if (orderService.updateOrder(order)) {
				// 支付成功后，下架对应的商品
				if (order.getIdleItem() != null && order.getIdleItem().getId() != null) {
					IdleItemModel idleItem = new IdleItemModel();
					idleItem.setId(order.getIdleItem().getId());
					idleItem.setIdleStatus((byte) 2); // 下架
					idleItemService.updateIdleItem(idleItem);
					System.out.println("========== 商品已下架，商品ID: " + order.getIdleItem().getId() + " ==========");
				}
				
				System.out.println("========== 模拟支付成功 ==========");
				System.out.println("订单号: " + order.getOrderNumber());
				System.out.println("支付金额: " + order.getOrderPrice());
				
				return ResultVo.success(order);
			} else {
				System.err.println("========== 模拟支付失败：更新订单状态失败 ==========");
				ResultVo<OrderModel> resultVo = ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
				resultVo.setMsg("支付失败，请稍后重试");
				return resultVo;
			}
		} catch (Exception e) {
			System.err.println("========== 模拟支付异常 ==========");
			System.err.println("异常信息: " + e.getMessage());
			e.printStackTrace();
			ResultVo<OrderModel> resultVo = ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
			resultVo.setMsg("支付异常: " + e.getMessage());
			return resultVo;
		}
	}

	@GetMapping("/pay") // subject=asdf&traceNo=1231&totalAmount=121
	public String pay(@RequestParam(value = "name",required = false) String name,
					  @RequestParam(value = "no",required = false) String no,
					  @RequestParam(value = "price",required = false) String price) {
		// 参数验证
		if (no == null || no.trim().isEmpty()) {
			System.err.println("支付失败：订单号不能为空");
			throw new RuntimeException("订单号不能为空");
		}
		if (price == null || price.trim().isEmpty()) {
			System.err.println("支付失败：支付金额不能为空");
			throw new RuntimeException("支付金额不能为空");
		}

		AlipayTradePagePayResponse response;
		try {
			System.out.println("========== 开始调用支付宝支付接口 ==========");
			System.out.println("订单号: " + no);
			System.out.println("支付金额: " + price);
			System.out.println("商品名称: " + (name != null ? name : "校园二手闲置物品交易平台"));

			// 发起API调用
			response = Factory.Payment.Page()
					.pay(name != null ? name : "校园二手闲置物品交易平台", no, price, "");

			System.out.println("========== 支付宝支付接口调用成功 ==========");
			System.out.println("响应内容长度: " + (response.getBody() != null ? response.getBody().length() : 0));
			System.out.println("***********************************************");
			System.out.println(response.getBody());
			System.out.println("***********************************************");

			return response.getBody();
		} catch (Exception e) {
			System.err.println("========== 支付宝支付接口调用失败 ==========");
			System.err.println("异常类型: " + e.getClass().getName());
			System.err.println("异常信息: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("支付接口调用失败: " + e.getMessage(), e);
		}
	}


}
