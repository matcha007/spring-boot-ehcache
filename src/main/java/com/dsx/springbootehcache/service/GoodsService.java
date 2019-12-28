package com.dsx.springbootehcache.service;

import com.dsx.springbootehcache.entity.Goods;
import com.dsx.springbootehcache.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 段顺兴
 * @create 2019/12/28 21:45
 * @description
 */
@Service
public class GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Cacheable(value = "goodsList")
    public List<Goods> list() {
        return goodsMapper.selectList(null);
    }

    @Cacheable(value = "goods",key = "#id")
    public Goods getGoodsById(int id) {
        return goodsMapper.selectById(id);
    }
}
