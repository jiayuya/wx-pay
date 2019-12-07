package cn.zjy.publicwxpxy.demo.dao;

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
    }

    public static BizResponse of(Integer code,Object data){
        BizResponse bizResponse = new BizResponse();
        bizResponse.setCode(code);
        bizResponse.setData(data);
        bizResponse.setDesc(DESC_MAP.get(code));
        return bizResponse;
    }
}
