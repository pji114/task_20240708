package com.task20240708.contact.entity;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.task20240708.contact.config.aop.validation.PhoneNumber;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer userId;

    String email;
    
    String name;

    //전화번호 양식 유혀성 검사 필드
    @PhoneNumber(message = "잘못된 전화번호 양식")
    String tel;

    Date joined;
}
