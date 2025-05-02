package com.example.chatserver.controller;

import com.example.chatserver.domain.Member;
import com.example.chatserver.dto.MemberSaveReqDto;
import com.example.chatserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto requestDto){

        Member member = memberService.create(requestDto);
        return ResponseEntity.ok(member.getId());
    }
}
