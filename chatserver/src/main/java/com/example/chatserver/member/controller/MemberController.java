package com.example.chatserver.member.controller;

import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto requestDto) {

        Member member = memberService.create(requestDto);
        return ResponseEntity.ok(member.getId());
    }
}
