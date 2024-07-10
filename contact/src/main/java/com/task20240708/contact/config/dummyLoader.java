/**
 * 프로그램 기동시 더미 데이터 삽입 설정
 */
package com.task20240708.contact.config;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.task20240708.contact.entity.Member;
import com.task20240708.contact.repository.MemberRepository;

@Component
public class dummyLoader implements CommandLineRunner{

    private final MemberRepository memberRepository;

    public dummyLoader(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDate localDate = localDateTime.toLocalDate();

        Member member1 = new Member();
        member1.setUserId(1);
        member1.setName("Park");
        member1.setEmail("pji114@naver.com");
        member1.setTel("010-1234-5678");
        member1.setJoined(Date.valueOf(localDate));

        memberRepository.save(member1);
    }
    
}
