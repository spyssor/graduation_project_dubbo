package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.api.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrder2018TMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrder2018T;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Service(interfaceClass = OrderServiceApi.class, group = "order2018")
public class DefaultOrderServiceImpl2018 implements OrderServiceApi {

    @Reference(interfaceClass = CinemaServiceApi.class, check = false)
    private CinemaServiceApi cinemaServiceApi;

    @Autowired
    private MoocOrder2018TMapper moocOrder2018TMapper;

    @Autowired
    private FTPUtil ftpUtil;

    //验证是否为真实的座位编号
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {

        //根据fieldId找到对应的座位位置图
        String seatPath = moocOrder2018TMapper.getSeatsByFieldId(fieldId);

        //读取位置图， 判断seats是否为真
        String fileStrByAddress = ftpUtil.getFileStrByAddress(seatPath);

        //将fileStrByAddress转换为JSON对象
        JSONObject jsonObject = JSONObject.parseObject(fileStrByAddress);

        //seats=1,2,3  ids="1,3,4,5,6,88..."
        String ids = jsonObject.get("ids").toString();

        //每一次匹配上的，都给isTrue +1
        String[] seatArr = seats.split(",");
        String[] idArr = ids.split(",");
        int isTrue = 0;
        for (String id : idArr){
            for (String seat : seatArr){
                if (seat.equalsIgnoreCase(id)){
                    isTrue ++;
                }
            }
        }

        //如果匹配上的数量与已售座位数一致，则表示全都匹配上了
        if (seatArr.length == isTrue){
            return true;
        }else{
            return false;
        }
    }

    //判断是否为已售座位
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id", fieldId);
        List<MoocOrder2018T> list = moocOrder2018TMapper.selectList(entityWrapper);
        String[] seatArr = seats.split(",");

        //如果有任何一个编号匹配上已售编号，则直接返回失败
        for (MoocOrder2018T moocOrderT : list){
            String[] ids = moocOrderT.getSeatsIds().split(",");
            for (String id : ids){
                for (String seat : seatArr){
                    if (id.equalsIgnoreCase(seat)){
                        return false;
                    }
                }
            }
        }

        return true;
    }


    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {

        //编号
        String uuid = UUIDUtil.genUuid();

        //影片信息
        FilmInfoVO filmInfoVO = cinemaServiceApi.getFilmInfoByFieldId(fieldId);
        Integer filmId = Integer.parseInt(filmInfoVO.getFilmId());

        //获取影院信息
        OrderQueryVO orderQueryVO = cinemaServiceApi.getOrderNeeds(fieldId);
        Integer cinemaId = Integer.parseInt(orderQueryVO.getCinemaId());
        double filmPrice = Double.parseDouble(orderQueryVO.getFilmPrice());

        //订单总金额
        int solds = soldSeats.split(",").length;
        double totalPrice = getTotalPrice(solds, filmPrice);

        MoocOrder2018T moocOrderT = new MoocOrder2018T();
        moocOrderT.setUuid(uuid);
        moocOrderT.setSeatsIds(soldSeats);
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setOrderUser(userId);
        moocOrderT.setOrderPrice(totalPrice);
        moocOrderT.setFilmPrice(filmPrice);
        moocOrderT.setFilmId(filmId);
        moocOrderT.setFieldId(fieldId);
        moocOrderT.setCinemaId(cinemaId);

        Integer insert = moocOrder2018TMapper.insert(moocOrderT);
        if (insert > 0){
            //返回查询结果
            OrderVO orderVO = moocOrder2018TMapper.getOrderInfoById(uuid);
            if (orderVO == null || orderVO.getOrderId() == null){
                log.error("订单信息查询失败，订单编号为{}", uuid);
                return null;
            }else{
                return orderVO;
            }
        }else{
            //插入错误
            log.error("订单插入失败，订单编号为{}", uuid);
            return null;
        }
    }

    private static double getTotalPrice(int solds, double filmPrice){

        BigDecimal soldsDcm = new BigDecimal(solds);
        BigDecimal  filmPriceDcm = new BigDecimal(filmPrice);

        BigDecimal totalDcm = soldsDcm.multiply(filmPriceDcm);

        //四舍五入，去小数点后两位
        BigDecimal result = totalDcm.setScale(2, RoundingMode.HALF_UP);

        return result.doubleValue();
    }

    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId, Page<OrderVO> page) {

        Page<OrderVO> result = new Page<>();
        if (userId == null){
            log.error("订单查询业务失败，用户编号未传入");
            return null;
        }else {
            List<OrderVO> ordersByUserId = moocOrder2018TMapper.getOrdersByUserId(userId, page);
            if (ordersByUserId == null && ordersByUserId.size() == 0){
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            }else{
                //获取订单总数
                EntityWrapper<MoocOrder2018T> entityWrapper = new EntityWrapper();
                entityWrapper.eq("order_user", userId);
                Integer counts = moocOrder2018TMapper.selectCount(entityWrapper);

                //将结果放入Page
                result.setTotal(counts);
                result.setRecords(ordersByUserId);

                return result;
            }
        }
    }

    //根据放映查询，获取所有的已售座位
    /*
        1  1,2,3,4
        1  5,6,7
     */
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {

        if (fieldId == null){
            log.error("查询已售座位错误，未传入任何场次编号");
            return "";
        }else{
            String soldSeatsByFieldId = moocOrder2018TMapper.getSoldSeatsByFieldId(fieldId);
            return soldSeatsByFieldId;
        }
    }

    @Override
    public OrderVO getOrderInfoById(String orderId) {

        OrderVO orderInfoById = moocOrder2018TMapper.getOrderInfoById(orderId);

        return orderInfoById;
    }

    @Override
    public boolean paySuccess(String orderId) {

        MoocOrder2018T moocOrder2018T = new MoocOrder2018T();

        moocOrder2018T.setUuid(orderId);
        moocOrder2018T.setOrderStatus(1);

        Integer integer = moocOrder2018TMapper.updateById(moocOrder2018T);

        if (integer >= 1){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean payFail(String orderId) {

        MoocOrder2018T moocOrder2018T = new MoocOrder2018T();

        moocOrder2018T.setUuid(orderId);
        moocOrder2018T.setOrderStatus(2);

        Integer integer = moocOrder2018TMapper.updateById(moocOrder2018T);

        if (integer >= 1){
            return true;
        }else{
            return false;
        }
    }
}
