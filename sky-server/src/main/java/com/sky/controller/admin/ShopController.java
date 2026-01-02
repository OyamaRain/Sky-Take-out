package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopStatusController")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";
    public static final Integer OPEN = 1;
    public static final Integer CLOSE = 2;

    //需要系统提前启动redis服务器,并配置redis的连接信息
    @Autowired
    private RedisTemplate redisTemplate;

    // 设置店铺状态
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺状态：{}", status == OPEN ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    // 获取店铺状态
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺状态:{}", status == OPEN ? "营业中" : "打烊中");
        return Result.success(status);
    }

}
