package cn.zjy.wxpay.demo.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@ConditionalOnClass(WxPayService.class)
@EnableConfigurationProperties(WxConfigProperties.class)
@Data
public class WxConfig {
    /**
     * 微信小程序配置
     */
    private static WxConfigProperties properties;

    /**
     * 用于向微信获取用户相关信息
     */
    private static WxMaService service;

    @Autowired
    public WxConfig(WxConfigProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取小程序配置
     * @return
     */
    public WxMaService getMaService() {
        if (service == null) {
            throw new IllegalArgumentException();
        }
        return service;
    }

    /**
     * 初始化小程序配置
     */
    @PostConstruct
    public void init() {
        if (properties == null) {
            throw new RuntimeException("请检查配置文件，添加下相关配置，注意别配错了！");
        }
        //配置小程序
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getAppid());
        config.setSecret(properties.getSecret());
        config.setToken(properties.getToken());
        config.setAesKey(properties.getAesKey());
        config.setMsgDataFormat(properties.getMsgDataFormat());

        service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public WxPayService wxService() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(this.properties.getAppid()));
        payConfig.setMchId(StringUtils.trimToNull(this.properties.getMchId()));
        payConfig.setMchKey(StringUtils.trimToNull(this.properties.getMchKey()));
        payConfig.setKeyPath(StringUtils.trimToNull(this.properties.getKeyPath()));
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }

}
