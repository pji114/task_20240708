package com.task20240708.contact.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintDeclarationException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.task20240708.contact.entity.Member;
import com.task20240708.contact.entity.MemberCsv;
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
     * @return 정산 처리 되었을 경우 https status 201을 리턴한다
     * @throws Exception 
     */
    @PostMapping("/members")
    public ResponseEntity<List<Member>> createMember(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody(required = false) String body, @RequestHeader("Content-Type") String contentType) throws Exception {

        List<Member> member = new ArrayList<>();
        List<MemberCsv> memberCsvs = new ArrayList<>();
        try{
            if(file != null) { //파일이 업로드된 경우 csv or json 형태로 분기
                String fileContentType = file.getContentType();
                
                if(JSON_TYPE.equals(fileContentType) || TEXT_TYPE.equals(fileContentType)) { //json 형태 일때
                    member = JsonUtils.pareJsonFile(file.getInputStream());
                    this.memberRepository.saveAll(member); //저장

                } else if(CSV_TYPE.equals(fileContentType) || EXCEL_TYPE.equals(fileContentType)) { //csv 형태 일때
                    memberCsvs = CsvUtils.parseCsvFile(new InputStreamReader(file.getInputStream()));
                    member = CsvUtils.memberCsvToMember(memberCsvs);
                    this.memberRepository.saveAll(member); //저장

                } else { //잘못된 타입
                    return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE); //415
                }

            } else if(body != null) { //post body로 데이터를 받았을 때
                
                if(JSON_TYPE.equals(contentType) || TEXT_TYPE.equals(contentType)) { //json 형태일때
                    InputStream inputStream = new ByteArrayInputStream(body.getBytes());
                    member = JsonUtils.pareJsonFile(inputStream);
                    this.memberRepository.saveAll(member); //저장

                } else if(CSV_TYPE.equals(contentType)) { //csv 형태일때
                    Reader reader = new StringReader(body);
                    memberCsvs = CsvUtils.parseCsvFile(reader);
                    member = CsvUtils.memberCsvToMember(memberCsvs);

                    this.memberRepository.saveAll(member); //저장

                } else {
                    return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE); //415
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); //204
            }

            return new ResponseEntity<>(HttpStatus.CREATED); //201
        } catch(ConstraintViolationException e){
            throw new ConstraintDeclarationException();
        } catch(IOException e){
            throw new Exception(e);
        }
    }

    /**
     * 사용자 전체 조회
     * @return
     * @throws Exception 
     */
    @GetMapping("/members")
    public ResponseEntity<List<Member>> getMembers() throws Exception{

        try{
            List<Member> members = this.memberRepository.findAll();

            if(members.size() > 0) {
                return new ResponseEntity<>(members, HttpStatus.OK); //200
            } else {
                return new ResponseEntity<>(members, HttpStatus.NO_CONTENT); //204
            }
            
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    /**
     * 특정 사용자 조회
     * 이름으로 조회시 동명이인이 있을 수 있기 때문에 List 형태로 리턴
     * @param name
     * @return 정상 처리 되었을 경우 조회된 List<Member>를 리턴한다
     * @throws Exception 
     */
    @GetMapping("/member/{name}")
    public ResponseEntity<List<Member>> getMember(@PathVariable String name) throws Exception{

        try{
            List<Member> members = this.memberRepository.findByName(name);

            if(members != null && !members.isEmpty() && members.size() > 0) {
                return new ResponseEntity<>(members, HttpStatus.OK); //200
            } else {
                return new ResponseEntity<>(members, HttpStatus.NO_CONTENT); //204
            }
            
        }catch(Exception e){
            throw new Exception(e);
        }
    }

    /**
     * email 기준으로 단일 사용자 삭제
     * @param email
     * @return
     * @throws Exception 
     */
    @DeleteMapping("/member/{email}")
    public ResponseEntity<Integer> deleteMember(@PathVariable String email) throws Exception{

        try{

            int delCnt = this.memberRepository.deleteByEmail(email);

            if(delCnt > 0) {
                return new ResponseEntity<>(delCnt, HttpStatus.OK); //200
            } else {
                return new ResponseEntity<>(delCnt, HttpStatus.NO_CONTENT); //204
            }

        }catch(Exception e){
            throw new Exception(e);
        }
    }

    /**
     * email을 키값으로 받고 전화번호를 수정한다.
     * @param email
     * @param reqMember
     * @return 정상 처리 되었을 경우 변경된 로우 수를 리턴한다
     * @throws Exception 
     */
    @PatchMapping("/member/{email}")
    public ResponseEntity<Integer> changeTel(@PathVariable String email, @RequestBody Member reqMember) throws Exception{

        try{
            Member member = this.memberRepository.findByEmail(email);

            if(member == null) { //email이 없는 사용자는 없다
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); //204
            } else {
                int updateCnt = this.memberRepository.updateTel(reqMember.getTel(), email);
                return new ResponseEntity<>(updateCnt, HttpStatus.OK); //200
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}

/**
 * todo
 * Iac ..?
 */
