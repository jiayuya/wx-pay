package cn.zjy.wxpay.demo.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BizResponse {
    private Integer code;
    private String desc;
    private Object data;

    private static Map<Integer,String> DESC_MAP = new HashMap<>();
    {
        DESC_MAP.put(0,"成功");
        DESC_MAP.put(1000,"失败");
//        DESC_MAP.put(1001,"账号或密码输入错误");
//        DESC_MAP.put(1002,"余额不足");
//        DESC_MAP.put(1003,"该手机号以被注册");
//        DESC_MAP.put(1004,"该身份证号以被注册");
//        DESC_MAP.put(1005,"交易密码输入错误");
//        DESC_MAP.put(1006,"查找不到该用户");
//        DESC_MAP.put(1007,"账户名输入错误");
    }

    public static BizResponse of(Integer code,Object data){
        BizResponse bizResponse = new BizResponse();
        bizResponse.setCode(code);
        bizResponse.setData(data);
        bizResponse.setDesc(DESC_MAP.get(code));
        return bizResponse;
    }
}
