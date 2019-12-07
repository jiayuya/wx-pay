package cn.zjy.wxpay.demo.controller;


import cn.zjy.wxpay.demo.bean.BizResponse;
import cn.zjy.wxpay.demo.bean.Order;
import cn.zjy.wxpay.demo.mapper.OrderMapper;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/wx")
public class WxController {

    @Autowired
    WxPayService wxPayService;

    @Resource
    OrderMapper orderMapper;

    @GetMapping("/pay")
    public BizResponse pay(@RequestParam String orderId, @RequestParam String openId) throws WxPayException {

        UUID uuid = UUID.randomUUID();
        StringBuffer outTradeNo = new StringBuffer();
        Arrays.stream(uuid.toString().split("-")).forEach(temp -> outTradeNo.append(temp));
        System.out.println("uuid = " + outTradeNo);

        WxPayUnifiedOrderRequest wxPay = WxPayUnifiedOrderRequest.newBuilder()
                //描述
                .body("zjy-微信付款")
                //支付金额
                .totalFee(1)
                //自己ip
                .spbillCreateIp("192.168.6.24")
                //回调地址
                .notifyUrl("https://wx.panqingshan.cn/notice/zjy/wx/pay")
                //交易类型 -- JSAPI公众号支付、NATIVE原生扫码支付、APP--app支付，统一下单接口trade_type的传参可参考这里
                .tradeType(WxPayConstants.TradeType.JSAPI)
                //openid
                .openid(openId)
                //交易单号
                .outTradeNo(outTradeNo.toString())
                .build();
        Object order = wxPayService.createOrder(wxPay);
        System.out.println("order = " + order);

        orderMapper.updateOutTradeNoByOrderId(orderId,outTradeNo.toString());

        return BizResponse.of(0,order);
    }

    @PostMapping("/pay")
    public String payCallback(@RequestBody String data) throws WxPayException {
        WxPayOrderNotifyResult payResult = wxPayService.parseOrderNotifyResult(data);
        System.out.println("result = " + payResult);
        String code = payResult.getReturnCode();
        System.out.println("code = " + code);
        if (StringUtils.isNotEmpty(code) && code.equals("SUCCESS")) {
            //修改订单状态（已支付）
            orderMapper.updateStatusByOutTradeNo(payResult.getOutTradeNo(),1);
            return "SUCCESS";
        }
        return "FAIL";
    }

    @GetMapping("/refund")
    public BizResponse refund(@RequestParam Long orderId) throws WxPayException {
        Order order = orderMapper.queryById(orderId);

        WxPayRefundRequest wxRefund = WxPayRefundRequest.newBuilder()
                //交易单号
                .outTradeNo(order.getOutTradeNo())
                //退款单号
                .outRefundNo(orderId + "" + new Random().nextInt(1000))
                //总金额
                .totalFee(1)
                //退款金额
                .refundFee(1)
                //回调地址
                .notifyUrl("https://wx.panqingshan.cn/notice/zjy/wx/refund")
                .build();
        wxPayService.refund(wxRefund);
        return BizResponse.of(0,null);
    };

    @PostMapping("/refund")
    public String refundCallback(@RequestBody String data) throws WxPayException {
        WxPayRefundNotifyResult refundResult = wxPayService.parseRefundNotifyResult(data);
        System.out.println("refundResult = " + refundResult);

        String code = refundResult.getReturnCode();
        if (StringUtils.isNotEmpty(code) && code.equals("SUCCESS")) {
            //修改订单状态（已退款）
            orderMapper.updateStatusByOutTradeNo(refundResult.getReqInfo().getOutTradeNo(),2);
            return "SUCCESS";
        }
        return "FAIL";
    }

}
