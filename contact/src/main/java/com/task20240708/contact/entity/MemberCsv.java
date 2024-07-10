package com.task20240708.contact.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberCsv implements Cloneable{

    Integer userId;

    String email;
    
    String name;

    String tel;

    String joined;
}
