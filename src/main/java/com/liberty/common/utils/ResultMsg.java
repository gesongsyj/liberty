package com.liberty.common.utils;

/**
* @ClassName: ResultMsg
* @Description: 封装返回的结果
* @author: Administrator
* @date: 2017年5月4日
* @version:1.0
 */
public class ResultMsg {
    private int errcode;
    private String errmsg;
    private Object p2pdata;

    public ResultMsg(int ErrCode, String ErrMsg, Object P2pData) {
        this.errcode = ErrCode;
        this.errmsg = ErrMsg;
        this.p2pdata = P2pData;
    }

    public ResultMsg(ResultStatusCode rsc, Object P2pData) {
        this.errcode = rsc.getErrcode();
        this.errmsg = rsc.getErrmsg();
        this.p2pdata = P2pData;
    }

    public ResultMsg(ResultStatusCode rsc) {
        this.errcode = rsc.getErrcode();
        this.errmsg = rsc.getErrmsg();
        this.p2pdata = null;
    }

    public ResultMsg(boolean res) {
        if (res) {
            this.errcode = ResultStatusCode.OK.getErrcode();
            this.errmsg = ResultStatusCode.OK.getErrmsg();
            this.p2pdata = null;
        } else {
            this.errcode = ResultStatusCode.FALSE.getErrcode();
            this.errmsg = ResultStatusCode.FALSE.getErrmsg();
            this.p2pdata = null;
        }
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Object getP2pdata() {
        return p2pdata;
    }

    public void setP2pdata(Object p2pdata) {
        this.p2pdata = p2pdata;
    }
}  
