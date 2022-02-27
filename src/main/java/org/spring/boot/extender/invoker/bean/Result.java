package org.spring.boot.extender.invoker.bean;

public class Result  {

    private User data;
    private String code;
    private String message;

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
