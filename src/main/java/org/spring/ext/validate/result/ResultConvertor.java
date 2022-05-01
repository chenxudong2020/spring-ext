package org.spring.ext.validate.result;



public class ResultConvertor {

     public Object convert(Result result){
        return RestMessage.newInstance(result.getSuccess(),result.getMessage());
    }
}
