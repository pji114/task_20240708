/**
 * CSV 파일을 핸들링 하는 유틸리티 클래스
 */

package com.task20240708.contact.utils;
import java.io.Reader;
import java.util.List;

import com.opencsv.bean.CsvToBeanBuilder;
import com.task20240708.contact.entity.Member;

public class CsvUtils {

    //csv 파일 읽어서 Member Class로 파싱
    public static List<Member> parseCsvFile(Reader reader) {
        return new CsvToBeanBuilder<Member>(reader)
        .withType(Member.class)
        .build()
        .parse();
    }
}
