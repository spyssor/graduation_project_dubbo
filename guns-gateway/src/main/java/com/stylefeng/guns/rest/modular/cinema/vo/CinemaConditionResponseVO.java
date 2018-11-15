package com.stylefeng.guns.rest.modular.cinema.vo;

import com.stylefeng.guns.api.cinema.vo.AreaVO;
import com.stylefeng.guns.api.cinema.vo.BrandVO;
import com.stylefeng.guns.api.cinema.vo.HallTypeVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CinemaConditionResponseVO implements Serializable {

    private List<BrandVO> brandList;
    private List<AreaVO> areaList;
    private List<HallTypeVO> hallTypeList;

}
