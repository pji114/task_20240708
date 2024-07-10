package com.task20240708.contact.config.aop.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 전화번호 양식의 정규식을 이용한 벨리데이터
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    //전화번호 정규식
    private static final String TEL_NUMBER_PATTERN = "^[0-9]{3}-[0-9]{4}-[0-9]{4}$";

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String tel, ConstraintValidatorContext context) {

        if(tel == null) return false;

        return tel.matches(TEL_NUMBER_PATTERN);
    }
    
}
