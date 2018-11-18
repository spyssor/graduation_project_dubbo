package com.stylefeng.guns.rest.modular.order;

import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/order/")
public class OrderController {

    //购票
    @RequestMapping(value = "buyTickets", method = RequestMethod.POST)
    public ResponseVO buyTickets(Integer fieldId, String soldSeats, String seatsName){

        //验证售出的票是否为真


        //已经销售的座位里，有没有这些座位


        //创建订单信息,注意获取登陆人


        return null;
    }

    //获取订单
    @RequestMapping(value = "getOrderInfo", method = RequestMethod.POST)
    public ResponseVO getOrderInfo(@RequestParam(name = "nowPage", required = false, defaultValue = "1") Integer nowPage,
                                   @RequestParam(name = "pageSize", required = false, defaultValue = "5") Integer pageSize){

        //获取当前登陆人的信息


        //使用当前登陆人，获取已经购买的订单


        return null;
    }
}
