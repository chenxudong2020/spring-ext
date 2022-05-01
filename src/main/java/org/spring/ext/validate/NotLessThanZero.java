package org.spring.ext.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * <p>
 *
 * </p>
 *
 * @author lizhe
 * @since 2020-10-19
 */
@Constraint(validatedBy = {NotLessThanZeroValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotLessThanZero {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}