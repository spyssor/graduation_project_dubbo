package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.CinemaQueryVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cinema/")
public class CinemaController {

    @Reference(interfaceClass = CinemaServiceApi.class)
    private CinemaServiceApi cinemaServiceApi;

    @RequestMapping(value = "getCinemas", method = RequestMethod.GET)
    public ResponseVO getCinemas(CinemaQueryVO cinemaQueryVO){

        //按照五个条件进行筛选


        //判断是否有满足条件的影院


        //如果出现异常


        return null;
    }

    @RequestMapping(value = "getCondition", method = RequestMethod.GET)
    public ResponseVO getCondition(CinemaQueryVO cinemaQueryVO){


        return null;
    }

    @RequestMapping(value = "getFields", method = RequestMethod.GET)
    public ResponseVO getFields(Integer cinemaId){

        return null;
    }

    @RequestMapping(value = "getFieldInfo", method = RequestMethod.POST)
    public ResponseVO getFieldInfo(Integer cinemaId, Integer fieldId){

        return null;
    }

}
