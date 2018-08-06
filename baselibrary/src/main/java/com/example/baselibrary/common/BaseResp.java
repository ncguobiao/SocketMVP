package com.example.baselibrary.common;

import java.io.Serializable;

/**
 * Created by Alienware on 2018/6/25.
 */

public class BaseResp implements Serializable{
    public BaseResp() {
    }

    private String returnCode;
    private String returnMsg;
    private Object retnrnJson;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public Object getRetnrnJson() {
        return retnrnJson;
    }

    public void setRetnrnJson(Object retnrnJson) {
        this.retnrnJson = retnrnJson;
    }
}
