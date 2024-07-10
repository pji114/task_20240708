package com.task20240708.contact.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.task20240708.contact.entity.Member;


public interface MemberRepository extends JpaRepository<Member, Integer>{
    List<Member> findByName(String name);

    Member findByEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM Member m WHERE m.email = :email")
    int deleteByEmail(String email);

    //String jpql = "UPDATE User u SET u.tel = :email WHERE u.id = :id";
    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.tel = :tel WHERE m.email = :email")
    int updateTel(String tel, String email);
}