package com.task20240708.contact.config;

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
        // TODO Auto-generated method stub

        Member member1 = new Member();
        member1.setUserId(1);
        member1.setUserName("pji114");

        memberRepository.save(member1);
    }
    
}
