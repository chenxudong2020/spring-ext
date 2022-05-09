package org.spring.ext.validate.result;

/**
 * @author 87260
 */
public enum ResultContain {

    /**
     * 结果包含message 类型String 默认值message
     */
    message("message",String.class,"message"),
    /**
     * 结果包含msg 类型String 默认值msg
     */
    msg("msg",String.class,"msg"),
    /**
     * 结果包含success 类型Boolean 默认值false
     */
    success("success",Boolean.class,false),
    // 结果默认出错结果集
    data("data",Object.class,null),
    //出现错误给出code 500
    codeInt("code",Integer.class,500),
    //出现错误给出0
    codeString("code",String.class,"0");


    private String resultType;
    private Class defaultType;
    private Object defaultValue;

    ResultContain(String resultType,Object defaultType, Object defaultValue) {
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Class getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
