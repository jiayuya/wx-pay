package cn.zjy.publicwxpxy.demo.controller;
import cn.zjy.publicwxpxy.demo.dao.BizResponse;
import cn.zjy.publicwxpxy.demo.dao.Order;
import cn.zjy.publicwxpxy.demo.mapper.OrderMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderMapper orderMapper;

    @GetMapping
    public BizResponse query(HttpServletRequest request){
        String openId = (String)request.getSession().getAttribute("openId");
        System.out.println("openId = " + openId);
        List<Order> list = orderMapper.query(openId);
        System.out.println("list = " + list);
        return BizResponse.of(0,list);
    }

    @PostMapping
    public BizResponse add(HttpServletRequest request){
        String openId = (String)request.getSession().getAttribute("openId");
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
