package com.yvliangbao.common.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 微信工具类
 *
 * @author 余量宝
 */
@Slf4j
@Component
public class WechatUtil {

    @Value("${wechat.miniapp.appid:}")
    private String appid;

    @Value("${wechat.miniapp.secret:}")
    private String secret;

    /**
     * 微信登录地址
     */
    private static final String JSCODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={0}&secret={1}&js_code={2}&grant_type=authorization_code";

    /**
     * 获取微信 openid 和 session_key
     *
     * @param code 微信登录code
     * @return {openid, session_key}
     */
    public JSONObject code2Session(String code) {
        try {
            // 检查配置
            if (appid == null || appid.isEmpty() || secret == null || secret.isEmpty()) {
                log.warn("微信小程序未配置，使用模拟登录");
                return mockLogin(code);
            }

            // 调用微信接口
            String url = JSCODE2SESSION_URL
                    .replace("{0}", appid)
                    .replace("{1}", secret)
                    .replace("{2}", code);

            String result = HttpUtil.get(url);
            log.info("微信登录响应: {}", result);

            JSONObject json = JSON.parseObject(result);

            // 检查错误
            if (json.containsKey("errcode")) {
                Integer errcode = json.getInteger("errcode");
                if (errcode != 0) {
                    log.error("微信登录失败: {}", json.getString("errmsg"));
                    return null;
                }
            }

            return json;
        } catch (Exception e) {
            log.error("调用微信接口失败", e);
            return null;
        }
    }

    /**
     * 模拟登录（开发测试用）
     * 注意：开发者工具中每次 wx.login() 返回的 code 都不同
     * 为了保持登录状态一致性，使用固定的 mock openid
     *
     * @param code 登录code
     * @return 模拟的用户信息
     */
    private JSONObject mockLogin(String code) {
        JSONObject json = new JSONObject();
        // 使用固定的 mock openid，这样在开发者工具中测试时不会每次都创建新用户
        json.put("openid", "mock_openid_dev_test");
        json.put("session_key", "mock_session_key");
        log.info("使用模拟登录，固定openid: mock_openid_dev_test");
        return json;
    }
}
