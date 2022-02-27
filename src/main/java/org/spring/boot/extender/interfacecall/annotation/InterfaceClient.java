package org.spring.boot.extender.interfacecall.annotation;

import org.spring.boot.extender.validate.ImportValidateController;
import org.spring.boot.extender.validate.NotLessThanZeroValidator;
import org.springframework.context.annotation.Import;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;



@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ImportValidateController.class)
public @interface InterfaceClient {

   String value();
}