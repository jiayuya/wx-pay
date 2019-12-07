package cn.zjy.wxpay.demo.controller;

import cn.zjy.wxpay.demo.bean.BizResponse;
import cn.zjy.wxpay.demo.bean.Order;
import cn.zjy.wxpay.demo.mapper.OrderMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderMapper orderMapper;

    @GetMapping
    public BizResponse query(@RequestParam String openId){
        List<Order> list = orderMapper.query(openId);
        return BizResponse.of(0,list);
    }

    @PostMapping
    public BizResponse add(@RequestBody String openId){
        Order order = new Order(System.currentTimeMillis(),openId,0,0.01,"");
        orderMapper.add(order);
        return BizResponse.of(0,null);
    }

    @GetMapping("/status")
    public BizResponse queryStatus(@RequestParam Integer status,@RequestParam Long orderId){
        System.out.println("status = " + status);
        Boolean bool = orderMapper.queryStatus(status,orderId);
        System.out.println("bool = " + bool);
        return BizResponse.of(0,bool);
    }

}
