package cn.zjy.publicwxpxy.demo.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long orderId;
    private String openId;
    private Integer status;
    private Double price;
    private String outTradeNo;
}
