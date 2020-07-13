package com.qukan.qkrecordupload.bean;

import lombok.Data;

/**
 * Created by Administrator on 2017/9/11 0011.
 */
@Data
public class UserInfoBean {
    private long id;//          "id": 1471586039244973,
    private long userId;//       "userId": 1471586039244973,
    private String name="";//"name": "zjn",
    private String remark="";//   "remark": "",
    private String memberName="";// 姓名      "memberName": "",
    private String telephone="";//     "telephone": "",
    private String headImage="";//     "headImage": "",
    private long createTime;//        "createTime": 1471586039000,
    private boolean expired;//    "expired": false,
    private boolean mainAccount;//    "mainAccount": false
}
