package com.dsx.springbootehcache.controller;

import com.alibaba.fastjson.JSON;
import com.dsx.springbootehcache.entity.Goods;
import com.dsx.springbootehcache.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 段顺兴
 * @create 2019/12/28 21:45
 * @description
 */
@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("list")
    public String list() {
       List<Goods> goodsList = goodsService.list();
        return JSON.toJSONString(goodsList);
    }

    @GetMapping("edit/{id}")
    public String edit(@PathVariable Integer id){
        Goods goods = goodsService.getGoodsById(id);
        return JSON.toJSONString(goods);
    }

}
