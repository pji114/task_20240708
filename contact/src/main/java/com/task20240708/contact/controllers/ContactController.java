package com.task20240708.contact.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.task20240708.contact.entity.Member;
import com.task20240708.contact.repository.MemberRepository;
import com.task20240708.contact.utils.CsvUtils;
import com.task20240708.contact.utils.JsonUtils;

@RestController
@RequestMapping("/api/v1")
public class ContactController {

    private final String JSON_TYPE = "application/json";
    private final String TEXT_TYPE = "text/json";
    private final String CSV_TYPE = "text/csv";
    private final String EXCEL_TYPE = "application/vnd.ms-excel";

    private final MemberRepository memberRepository;

    public ContactController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 비상연락처 멤버를 추가한다
     * @param file csv 또는 json 파일로 추가될 파일
     * @param body post body
     * @param contentType
     * @return
     */
    @PostMapping("/members")
    public String createMember(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody(required = false) String body, @RequestHeader("Content-Type") String contentType) {

        List<Member> member;
        try{
            if(file != null) {
                String fileContentType = file.getContentType();
                //파일이 업로드된 경우 csv or json 형태로 분기
                if(JSON_TYPE.equals(fileContentType) || TEXT_TYPE.equals(fileContentType)) { //json 형태 일때
                    member = JsonUtils.pareJsonFile(file.getInputStream());
                    this.memberRepository.saveAll(member); //저장

                } else if(CSV_TYPE.equals(fileContentType) || EXCEL_TYPE.equals(fileContentType)) { //csv 형태 일때
                    member = CsvUtils.parseCsvFile(new InputStreamReader(file.getInputStream()));
                    this.memberRepository.saveAll(member); //저장

                } else { //잘못된 타입
                    return "Unsupported File Type";
                }

            } else if(body != null) {
                //post body로 데이터를 받았을 때
                if(JSON_TYPE.equals(contentType) || TEXT_TYPE.equals(contentType)) { //json 형태 일대
                    InputStream inputStream = new ByteArrayInputStream(body.getBytes());
                    member = JsonUtils.pareJsonFile(inputStream);
                    this.memberRepository.saveAll(member); //저장

                } else if(CSV_TYPE.equals(contentType)) {
                    Reader reader = new StringReader(body);
                    member = CsvUtils.parseCsvFile(reader);
                    this.memberRepository.saveAll(member); //저장

                } else {
                    return "Unsupported Content type";
                }
            } else {
                return "No Data";
            }

            return "Success";
        } catch(IOException e){
            e.printStackTrace();
            return "Error";
        }
    }

    @GetMapping("/members")
    public List<Member> getMembers(){
        return this.memberRepository.findAll();
    }

    @GetMapping("/member/{name}")
    public List<Member> getMember(@PathVariable String name){
        return this.memberRepository.findByName(name);
    }

    /**
     * todo
     * 
     * 생성 성공시 http status 201 create response
     * delete - 부분 삭제
     * put & patch
     * exception handler
     * 파라미터 유효성 검증
     */

    
}
