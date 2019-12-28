package com.dsx.springbootehcache.entity;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * @author 段顺兴
 * @create 2019/12/28 21:43
 * @description
 */
public class Goods implements Serializable {
    private static final long serialVersionUID = -5748816679699192640L;

    @TableId
    private Integer id;
    private String name;
    private String bumenNo;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getBumenNo() {
        return bumenNo;
    }

    public void setBumenNo(String bumenNo) {
        this.bumenNo = bumenNo;
    }
}
