package com.kingskys.http;

public class HttpResult {
    public boolean success = false;
    public int code;
    public String data;

    HttpResult(boolean success, int code, String data) {
        this.success = success;
        this.code = code;
        this.data = data;
    }
}
