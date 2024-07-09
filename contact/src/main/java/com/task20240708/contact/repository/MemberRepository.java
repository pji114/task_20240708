package com.task20240708.contact.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task20240708.contact.entity.Member;


public interface MemberRepository extends JpaRepository<Member, Integer>{
    List<Member> findByName(String name);
}