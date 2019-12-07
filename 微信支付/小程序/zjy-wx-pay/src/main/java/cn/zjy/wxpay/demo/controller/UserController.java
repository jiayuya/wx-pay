package cn.zjy.wxpay.demo.controller;

import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.zjy.wxpay.demo.bean.BizResponse;
import cn.zjy.wxpay.demo.config.WxConfig;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private WxConfig wxConfig;

    @GetMapping
    public BizResponse login(@RequestParam String code) throws WxErrorException {
        WxMaUserService userService = wxConfig.getMaService().getUserService();
        WxMaJscode2SessionResult sessionInfo = userService.getSessionInfo(code);
        String openid = sessionInfo.getOpenid();
        Integer state = 0;
        if(openid == null) state = 1000;
        return BizResponse.of(state,openid);
    }

}
