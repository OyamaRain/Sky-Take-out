package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    // 提交订单
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    // 订单历史查询(User)
    PageResult pageQuery4User(int page, int pageSize, Integer status);

    // 根据id查询订单详情
    OrderVO getById(Long id);

    // 取消订单
    void cancelById(Long id);

    // 再来一单
    void repetition(Long id);

    // 条件搜索订单(Admin)
    PageResult pageQuery4Admin(OrdersPageQueryDTO ordersPageQueryDTO);

    //各个状态的订单数量统计
    OrderStatisticsVO statistics();

    // 接单
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    // 拒单
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    // 取消订单
    void cancel(OrdersCancelDTO ordersCancelDTO);

    // 派送订单
    void delivery(Long id);

    // 完成订单
    void complete(Long id);
}
