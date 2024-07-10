package com.task20240708.contact.config.aop.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 검증할 필드를 어노테이션으로 등록
 */
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "잘못된 전화번호 양식";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}