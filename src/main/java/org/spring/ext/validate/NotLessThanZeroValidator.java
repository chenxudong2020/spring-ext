package org.spring.ext.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * <p>
 *
 * </p>
 *
 * @author chenxudong
 * @since 2020-10-19
 */
public class NotLessThanZeroValidator implements ConstraintValidator<NotLessThanZero, Object> {

    @Override
    public void initialize(NotLessThanZero notLessThanZero) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (null == value) {
            return true;
        }
        if (value instanceof Number) {
            if (((Number) value).intValue() > 0) {
                return true;
            }
        }
        return false;
    }

}