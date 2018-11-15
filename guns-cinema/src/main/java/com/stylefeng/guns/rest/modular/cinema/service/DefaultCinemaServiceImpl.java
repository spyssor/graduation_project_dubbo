package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaServiceApi;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.MoocAreaDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocBrandDictT;
import com.stylefeng.guns.rest.common.persistence.model.MoocCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MoocHallDictT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CinemaServiceApi.class)
public class DefaultCinemaServiceImpl implements CinemaServiceApi {

    @Autowired
    private MoocCinemaTMapper moocCinemaTMapper;

    @Autowired
    private MoocAreaDictTMapper moocAreaDictTMapper;

    @Autowired
    private MoocBrandDictTMapper moocBrandDictTMapper;

    @Autowired
    private MoocHallDictTMapper moocHallDictTMapper;

    @Autowired
    private MoocHallFilmInfoTMapper moocHallFilmInfoTMapper;

    @Autowired
    private MoocFieldTMapper moocFieldTMapper;

    //    1、根据CinemaQueryVO，查询影院列表
    @Override
    public Page<CinemaVO> getCinema(CinemaQueryVO cinemaQueryVO){

        //业务实体集合
        List<CinemaVO> cinemas = new ArrayList<>();

        //判断是否传入查询条件 -> brandId,distId,hallType 是否=99
        Page<MoocCinemaT> page = new Page<>(cinemaQueryVO.getNowPage(), cinemaQueryVO.getPageSize());

        EntityWrapper<MoocCinemaT> entityWrapper = new EntityWrapper<>();
        if(cinemaQueryVO.getBrandId() != 99){
            entityWrapper.eq("brand_id", cinemaQueryVO.getBrandId());
        }
        if(cinemaQueryVO.getDistrictId() != 99){
            entityWrapper.eq("area_id", cinemaQueryVO.getDistrictId());
        }
        if(cinemaQueryVO.getHallType() != 99){
            entityWrapper.like("hall_ids", "%#"+cinemaQueryVO.getHallType()+"#%");
        }

        //数据实体 -> 业务实体
        List<MoocCinemaT> moocCinemaTS = moocCinemaTMapper.selectPage(page, entityWrapper);
        for (MoocCinemaT moocCinemaT : moocCinemaTS){
            CinemaVO cinemaVO = new CinemaVO();

            cinemaVO.setUuid(moocCinemaT.getUuid()+"");
            cinemaVO.setMinimumPrice(moocCinemaT.getMinimumPrice()+"");
            cinemaVO.setAddress(moocCinemaT.getCinemaAddress());
            cinemaVO.setCinemaName(moocCinemaT.getCinemaName());

            cinemas.add(cinemaVO);
        }

        //根据条件，判断影院列表总数
        long counts = moocCinemaTMapper.selectCount(entityWrapper);

        //组织返回对象
        Page<CinemaVO> result = new Page<>();
        result.setRecords(cinemas);
        result.setSize(cinemaQueryVO.getPageSize());
        result.setTotal(counts);

        return result;
    }

    //    2、根据条件获取品牌列表[除了99以外，其他的数字位isActive]
    @Override
    public List<BrandVO> getBrands(int brandId){

        boolean flag = false;
        List<BrandVO> brands = new ArrayList<>();

        //判断传入的brandId是否存在
        MoocBrandDictT moocBrandDictT = moocBrandDictTMapper.selectById(brandId);

        //判断brandId是否等于99
        if (brandId == 99 || moocBrandDictT == null || moocBrandDictT.getUuid() == null){
            flag = true;
        }

        //查询所有列表
        List<MoocBrandDictT> moocBrandDictTS = moocBrandDictTMapper.selectList(null);

        //判断flag如果为true，则将99置为true
        for (MoocBrandDictT brand : moocBrandDictTS){
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandName(brand.getShowName());
            brandVO.setBrandId(brand.getUuid()+"");

            //如果flag位true，则需要99，如果false，则匹配上的内容为brandId
            if (flag){
                if (brand.getUuid() == 99){
                    brandVO.setActive(true);
                }
            }else{
                if (brand.getUuid() == brandId){
                    brandVO.setActive(true);
                }
            }

            brands.add(brandVO);
        }

        return brands;
    }

    //    3、获取行政区域列表
    @Override
    public List<AreaVO> getAreas(int areaId){

        boolean flag = false;
        List<AreaVO> areas = new ArrayList<>();

        //判断传入的areaId是否存在
        MoocAreaDictT moocAreaDictT = moocAreaDictTMapper.selectById(areaId);

        //判断areaId是否等于99
        if (areaId == 99 || moocAreaDictT == null || moocAreaDictT.getUuid() == null){
            flag = true;
        }

        //查询所有列表
        List<MoocAreaDictT> moocAreaDictTS = moocAreaDictTMapper.selectList(null);

        //判断flag如果为true，则将99置为true
        for (MoocAreaDictT area : moocAreaDictTS){
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaName(area.getShowName());
            areaVO.setAreaId(area.getUuid()+"");

            //如果flag位true，则需要99，如果false，则匹配上的内容为brandId
            if (flag){
                if (area.getUuid() == 99){
                    areaVO.setActive(true);
                }
            }else{
                if (area.getUuid() == areaId){
                    areaVO.setActive(true);
                }
            }

            areas.add(areaVO);
        }

        return areas;
    }

    //    4、获取影厅类型列表
    @Override
    public List<HallTypeVO> getHallTypes(int hallTypeId){

        boolean flag = false;
        List<HallTypeVO> hallTypes = new ArrayList<>();

        //判断传入的areaId是否存在
        MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(hallTypeId);

        //判断areaId是否等于99
        if (hallTypeId == 99 || moocHallDictT == null || moocHallDictT.getUuid() == null){
            flag = true;
        }

        //查询所有列表
        List<MoocHallDictT> moocHallDictTS = moocHallDictTMapper.selectList(null);

        //判断flag如果为true，则将99置为true
        for (MoocHallDictT hallType : moocHallDictTS){
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHallTypeName(hallType.getShowName());
            hallTypeVO.setHallTypeId(hallType.getUuid()+"");

            //如果flag位true，则需要99，如果false，则匹配上的内容为brandId
            if (flag){
                if (hallType.getUuid() == 99){
                    hallTypeVO.setActive(true);
                }
            }else{
                if (hallType.getUuid() == hallTypeId){
                    hallTypeVO.setActive(true);
                }
            }

            hallTypes.add(hallTypeVO);
        }

        return hallTypes;
    }

    //    5、根据影院编号，获取影院信息
    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId){

        //数据实体
        MoocCinemaT moocCinemaT = moocCinemaTMapper.selectById(cinemaId);

        //数据实体 -> 业务实体
        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setCinemaAddress(moocCinemaT.getCinemaAddress());
        cinemaInfoVO.setImgUrl(moocCinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(moocCinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(moocCinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(moocCinemaT.getUuid()+"");

        return cinemaInfoVO;
    }

    //    6、获取所有电影的信息和对应的放映场次信息，根据影院编号
    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId){

        List<FilmInfoVO> filmInfos = moocFieldTMapper.getFilmInfos(cinemaId);

        return filmInfos;
    }

    //    7、根据放映场次ID获取放映信息
    @Override
    public HallInfoVO getFilmFieldInfo(int fieldId){

        HallInfoVO hallInfoVO = moocFieldTMapper.getHallInfo(fieldId);

        return hallInfoVO;
    }

    //    8、根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId){

        FilmInfoVO filmInfoVO = moocFieldTMapper.getFilmInfoById(fieldId);

        return filmInfoVO;
    }
}
