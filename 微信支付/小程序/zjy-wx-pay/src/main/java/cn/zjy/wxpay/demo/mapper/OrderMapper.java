package cn.zjy.wxpay.demo.mapper;

import cn.zjy.wxpay.demo.bean.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * 订单列表
     * @param openId
     * @return
     */
    @Select("SELECT order_id, `status`, open_id, price\n" +
            "\tFROM `order` WHERE open_id = #{openId}")
    List<Order> query(String openId);

    /**
     * 生成订单
     * @param order
     */
    @Insert("INSERT INTO `order`\n" +
            "\t(order_id, `status`, open_id, price)\n" +
            "\tVALUES (#{orderId}, 0, #{openId}, #{price})")
    void add(Order order);

    /**
     * 生成outTradeNo号
     * @param orderId
     * @param outTradeNo
     */
    @Update("UPDATE `order`\n" +
            "\tSET\n" +
            "\t\t`out_trade_no`= #{outTradeNo}\n" +
            "\tWHERE order_id = #{orderId}")
    void updateOutTradeNoByOrderId(String orderId,String outTradeNo);

    /**
     * 根据outTradeNo修改订单状态为已支付
     * @param outTradeNo
     * @param status
     * @return
     */
    @Update("UPDATE `order`\n" +
            "\tSET\n" +
            "\t\t`status`= #{status}\n" +
            "\tWHERE out_trade_no = #{outTradeNo}")
    Boolean updateStatusByOutTradeNo(String outTradeNo,Integer status);

    /**
     * 根据orderId修改状态
     * @param orderId
     * @param status
     * @return
     */
    @Update("UPDATE `order`\n" +
            "\tSET\n" +
            "\t\t`status`= #{status}\n" +
            "\tWHERE order_id = #{orderId}")
    Boolean updateStatus(Long orderId,Integer status);

    @Select("SELECT order_id, `status`, open_id, price, out_trade_no\n" +
            "\tFROM `order` WHERE order_id = #{orderId}")
    Order queryById(Long orderId);


    @Select("SELECT `status`\n" +
            "\tFROM `order` WHERE order_id = #{orderId} AND `status` = #{status}")
    Boolean queryStatus(Integer status, Long orderId);
}
