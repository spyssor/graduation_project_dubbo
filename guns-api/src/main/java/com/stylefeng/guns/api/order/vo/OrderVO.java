package com.stylefeng.guns.api.order.vo;

import lombok.Data;

import javax.security.sasl.SaslServer;
import java.io.Serializable;

@Data
public class OrderVO implements Serializable {

    private String orderId;
    private String filmName;
    private String fieldTime;
    private String cinemaTime;
    private String seatsName;
    private String orderPrice;
    private String orderTimestamp;
    private String orderStatus;
}
