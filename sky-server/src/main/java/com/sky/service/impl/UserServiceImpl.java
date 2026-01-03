package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //微信openid接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public UserLoginVO wxLogin(UserLoginDTO userLoginDTO) {
        //调用微信服务，获取openid
        String openid = getOpenid(userLoginDTO.getCode());
        log.info("获取openid:{}", openid);
        //判断openid是否存在，如果为空则抛出异常
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断openid是否为新用户，如果是新用户则自动完成注册
        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            //插入user表
            userMapper.insert(user);
        }
        //生成jwt令牌
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO userLoginVo = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return userLoginVo;
    }


    //获取openid
    private String getOpenid(String code){
        Map<String,String> params = new HashMap<>();
        params.put("appid",weChatProperties.getAppid());
        params.put("secret",weChatProperties.getSecret());
        params.put("js_code",code);
        params.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, params);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }

}
