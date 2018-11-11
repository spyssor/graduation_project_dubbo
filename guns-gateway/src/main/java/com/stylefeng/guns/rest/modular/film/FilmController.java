package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.film.FilmAsyncServiceApi;
import com.stylefeng.guns.api.film.FilmServiceApi;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/film/")
public class FilmController {

    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmServiceApi.class)
    private FilmServiceApi filmServiceApi;

    @Reference(interfaceClass = FilmAsyncServiceApi.class, async = true)
    private FilmAsyncServiceApi filmAsyncServiceApi;

    //获取首页信息接口
    /*
     * API网关：
     *     1、功能聚合【API聚合】
     *     好处：
     *         1、六个接口，一次请求，同一时刻节省了五次HTTP请求
     *         2、同一个接口对外暴露，降低了前后端分离开发的难度和复杂度
     *     坏处：
     *         1、一次获取数据过多，容易出现问题
     * */
    @RequestMapping(value = "getIndex", method = RequestMethod.GET)
    public ResponseVO getIndex(){
        //测试lombok
//        BannerVO bannerVO = new BannerVO();
//        bannerVO.setBannerAddress("");

        FilmIndexVO filmIndexVO = new FilmIndexVO();

        //获取banner信息
        filmIndexVO.setBanners(filmServiceApi.getBanners());

        //获取热映的电影信息
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true,8,1,1,99,99,99));

        //即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true, 8,1,1,99,99,99));

        //票房排行榜
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());

        //人气榜
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());

        //获取前100
        filmIndexVO.setTop100(filmServiceApi.getTop());

        return ResponseVO.success(IMG_PRE, filmIndexVO);
    }



    @RequestMapping(value = "getConditionList", method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(name = "catId", required = false, defaultValue = "99") String catId,
                                       @RequestParam(name = "sourceId", required = false, defaultValue = "99") String sourceId,
                                       @RequestParam(name = "yearId", required = false, defaultValue = "99") String yearId){

        FilmConditionVO filmConditionVO = new FilmConditionVO();

        //标识位
        boolean flag = false;

        //类型集合
        List<CatVO> cats = filmServiceApi.getCats();
        List<CatVO> catResult = new ArrayList<>();
        CatVO catVO = null;
        for (CatVO cat : cats){
            //判断集合是否存在catId,如果存在，则将对应的实体变成active状态
            //6
            //1,2,3,99,4,5
            /*
                优化;
                    1、数据层查询Id进行排序【有序集合 -> 有序数组】
                    2、通过二分查找
             */
            if (cat.getCatId().equals("99")){
                catVO = cat;
                continue;
            }

            if (cat.getCatId().equals(catId)){
                flag = true;
                cat.setActive(true);
            }else{
                cat.setActive(false);
            }

            catResult.add(cat);
        }

        //如果不存在，则默认将全部变为active状态
        if (!flag){
            catVO.setActive(true);
            catResult.add(catVO);
        }else{
            catVO.setActive(false);
            catResult.add(catVO);
        }


        //片源集合
        flag = false;
        List<SourceVO> sources = filmServiceApi.getSources();
        List<SourceVO> sourceResult = new ArrayList<>();
        SourceVO sourceVO = null;
        for (SourceVO source : sources){
            if (source.getSourceId().equals("99")){
                sourceVO = source;
                continue;
            }

            if (source.getSourceId().equals(sourceId)){
                flag = true;
                source.setActive(true);
            }else{
                source.setActive(false);
            }

            sourceResult.add(source);

        }

        if (!flag){
            sourceVO.setActive(true);
            sourceResult.add(sourceVO);
        }else{
            sourceVO.setActive(false);
            sourceResult.add(sourceVO);
        }

        //年代集合
        flag = false;
        List<YearVO> years = filmServiceApi.getYears();
        List<YearVO> yearResult = new ArrayList<>();
        YearVO yearVO = null;
        for (YearVO year : years){
            if (year.getYearId().equals("99")){
                yearVO = year;
                continue;
            }

            if (year.getYearId().equals(yearId)){
                flag = true;
                year.setActive(true);
            }else{
                year.setActive(false);
            }

            yearResult.add(year);

        }

        if (!flag){
            yearVO.setActive(true);
            yearResult.add(yearVO);
        }else{
            yearVO.setActive(false);
            yearResult.add(yearVO);
        }

        filmConditionVO.setCatInfo(catResult);
        filmConditionVO.setSourceInfo(sourceResult);
        filmConditionVO.setYearInfo(yearResult);

        return ResponseVO.success(filmConditionVO);
    }

    @RequestMapping(value = "getFilms", method = RequestMethod.GET)
    public ResponseVO getFilms(FilmRequestVO filmRequestVO){

        FilmVO filmVO = null;

        //根据showType判断影片查询类型
        switch (filmRequestVO.getShowType()){
            case 1:
                filmVO = filmServiceApi.getHotFilms(
                        false, filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getCatId());
                break;
            case 2:
                filmVO = filmServiceApi.getSoonFilms(
                        false, filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getCatId());
                break;
            case 3:
                filmVO = filmServiceApi.getClassicFilms(
                        filmRequestVO.getPageSize(), filmRequestVO.getNowPage(), filmRequestVO.getSortId(),
                        filmRequestVO.getSourceId(), filmRequestVO.getYearId(), filmRequestVO.getCatId());
                break;
            default:
                filmVO = filmServiceApi.getHotFilms(
                        false, filmRequestVO.getPageSize(), filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(), filmRequestVO.getYearId(),
                        filmRequestVO.getCatId());
                break;
        }

        //根据sortId排序
        //添加各种条件查询
        //判断当前是第几页


        return ResponseVO.success(
                filmVO.getNowPage(), filmVO.getTotalPage(),
                IMG_PRE, filmVO.getFilmInfo());
    }


    @RequestMapping(value = "films/{searchParam}", method = RequestMethod.GET)
    public ResponseVO films(@PathVariable("searchParam")String searchParam,
                            int searchType) throws ExecutionException, InterruptedException {

        //根据searchType，判断查询类型
        FilmDetailVO filmDetail = filmServiceApi.getFilmDetail(searchType,searchParam);

        if(filmDetail == null){
            return ResponseVO.serviceFail("没有可查询的影片");
        }else if(filmDetail.getFilmId() == null || filmDetail.getFilmId().trim().length() == 0){
            return ResponseVO.serviceFail("没有可查询的影片");
        }

        String filmId = filmDetail.getFilmId();

        //查询影片的详细信息 -> Dubbo的异步调用!!!(解决过类似的多个Service有延时的情况)
        //获取影片描述信息
//        FilmDescVO filmDescVO = filmAsyncServiceApi.getFilmDesc(filmId);
        filmAsyncServiceApi.getFilmDesc(filmId);
        Future<FilmDescVO> filmDescVOFuture = RpcContext.getContext().getFuture();

        //获取图片信息
//        ImgVO imgVO = filmAsyncServiceApi.getImgs(filmId);
        filmAsyncServiceApi.getImgs(filmId);
        Future<ImgVO> imgVOFuture = RpcContext.getContext().getFuture();

        //获取导演信息
//        ActorVO directorVO = filmAsyncServiceApi.getDectInfo(filmId);
        filmAsyncServiceApi.getDectInfo(filmId);
        Future<ActorVO> directorVOFuture = RpcContext.getContext().getFuture();

        //获取演员信息
//        List<ActorVO> actors = filmAsyncServiceApi.getActors(filmId);
        filmAsyncServiceApi.getActors(filmId);
        Future<List<ActorVO>> actorsVOFuture = RpcContext.getContext().getFuture();

        //组织Actor属性
        ActorRequestVO actorRequestVO = new ActorRequestVO();
        actorRequestVO.setActors(actorsVOFuture.get());
        actorRequestVO.setDirector(directorVOFuture.get());

        //组织info对象
        InfoRequestVO infoRequestVO = new InfoRequestVO();
        infoRequestVO.setActors(actorRequestVO);
        infoRequestVO.setBiography(filmDescVOFuture.get().getBiography());
        infoRequestVO.setFilmId(filmId);
        infoRequestVO.setImgVO(imgVOFuture.get());

        //组织成返回值
        filmDetail.setInfo04(infoRequestVO);

        return ResponseVO.success(IMG_PRE, filmDetail);
    }

}
