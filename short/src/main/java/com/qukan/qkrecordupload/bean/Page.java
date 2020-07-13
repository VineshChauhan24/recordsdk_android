package com.qukan.qkrecordupload.bean;

import java.util.List;

import lombok.Data;

/**
 * Created by Administrator on 2015/7/30 0030.
 */
@Data
public class Page<T>
{

    private int pageIndex = 1;// 页码

    private int pageSize = 10;// 页面大小

    private int pageTotal = 1; // 总页数

    private int amount = 0; // 记录总数

    private List<T> data;// 本页数据
}
