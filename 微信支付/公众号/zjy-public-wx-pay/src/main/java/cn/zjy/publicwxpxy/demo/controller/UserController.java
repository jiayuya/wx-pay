package cn.zjy.publicwxpxy.demo.controller;

import cn.zjy.publicwxpxy.demo.config.WxConfig;
import cn.zjy.publicwxpxy.demo.dao.BizResponse;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private WxConfig wxConfig;

    @GetMapping("/auth")
    public BizResponse auth(){
        String url = wxConfig.getMpService().oauth2buildAuthorizationUrl("http://wx.panqingshan.cn/", "snsapi_base", "ok");
        System.out.println("url = " + url);
        return BizResponse.of(0,url);
    }

    @GetMapping("/login")
    public BizResponse login(@RequestParam String code, HttpServletRequest request) throws WxErrorException {
        System.out.println("code = " + code);
        WxMpOAuth2AccessToken accessToken = wxConfig.getMpService().oauth2getAccessToken(code);
        String openId = accessToken.getOpenId();
        System.out.println("openId = " + openId);
        HttpSession session = request.getSession();
        session.setAttribute("openId",openId);
        return BizResponse.of(0,null);
    }

    @GetMapping("/info")
    public BizResponse getUserInfo(HttpServletRequest request) throws WxErrorException {
        String openId = (String)request.getSession().getAttribute("openId");
        System.out.println("openId = " + openId);
        WxMpUser userInfo = wxConfig.getMpService().getUserService().userInfo(openId);
        System.out.println("userInfo = " + userInfo);
        return BizResponse.of(0,userInfo);
    }

}
