/**
 * CSV 파일을 핸들링 하는 유틸리티 클래스
 */

package com.task20240708.contact.utils;
import java.io.Reader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.bean.CsvToBeanBuilder;
import com.task20240708.contact.entity.Member;
import com.task20240708.contact.entity.MemberCsv;

public class CsvUtils {

    //csv 파일 읽어서 MemberCsv Class로 파싱
    public static List<MemberCsv> parseCsvFile(Reader reader) {
        return new CsvToBeanBuilder<MemberCsv>(reader)
        .withType(MemberCsv.class)
        .build()
        .parse();
    }

    /**
     * 01045677890 의 전화번호를 010-1234-1234 으로 변경
     * @param csvTel
     * @return
     */
    public static String convertTelType(String csvTel){
        return csvTel.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }

    /**
     * String 형태의 yyyy-MM-dd 를 java.sql.Date 형태로 변경
     * @param joined
     * @return
     */
    public static Date convertJoinedType(String joined){
        // 문자열을 LocalDate로 변환
        LocalDate localDate = LocalDate.parse(joined.replaceAll("\\.", "-")
        , DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return Date.valueOf(localDate);
        
    }

    /**
     * List<MemberCsv> memberCsvs 를 Member Entity 에 저장 할 수 있도록 List<Member> 형태로 변경
     * @param memberCsvs
     * @return
     */
    public static List<Member> memberCsvToMember(List<MemberCsv> memberCsvs) {

        List<Member> members = new ArrayList<>();

        for (MemberCsv memberCsv : memberCsvs) {
            memberCsv.setTel(convertTelType(memberCsv.getTel()));

            Member member = new Member();
            member.setEmail(memberCsv.getEmail());
            member.setName(memberCsv.getName());
            member.setTel(memberCsv.getTel());
            member.setJoined(convertJoinedType(memberCsv.getJoined()));

            members.add(member);
        }

        return members;
    }
}
