package com.iecube.community.util;

import java.io.Serializable;

/**
 * json格式的数据进行数据响应
 */
public class JsonResult<E> implements Serializable {
    //响应的状态码
    private Integer state;

    //wangEditor响应码
    private Integer errno;

    //响应的描述信息
    private String message;

    //对应的数据  不确定的数据类型用泛型  方法中用到泛型类的声明中也要声明为泛型
    private E data;

    public JsonResult() {
    }

    public JsonResult(Integer state) {
        this.state = state;
    }

    public JsonResult(Throwable e) {
        this.message = e.getMessage();
    }

    public JsonResult(Integer state, E data) {
        this.state = state;
        this.data = data;
    }

    public JsonResult(Integer state, Integer errno, E data) {
        this.state = state;
        this.errno=errno;
        this.data = data;
    }


    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public void setErrno(Integer errno) {this.errno=errno;}

    public Integer getErrno() {return errno;}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
