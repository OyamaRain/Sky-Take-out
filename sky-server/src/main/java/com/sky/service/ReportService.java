package com.sky.service;

import com.sky.vo.*;

import java.time.LocalDate;

public interface ReportService {
    //营业额统计
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    //用户统计
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    //订单统计
    OrderReportVO orderReportVO(LocalDate begin, LocalDate end);

    //top10销量
    SalesTop10ReportVO salesTop10ReportVO(LocalDate begin, LocalDate end);
}
