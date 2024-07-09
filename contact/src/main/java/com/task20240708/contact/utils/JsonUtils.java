package com.task20240708.contact.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task20240708.contact.entity.Member;

public class JsonUtils {
    
    /**
     * JSON으로 된 파일을 읽어서 Member Class로 리턴
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static List<Member> pareJsonFile(InputStream inputStream) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(inputStream, new TypeReference<List<Member>>() {});
        
    }

}
