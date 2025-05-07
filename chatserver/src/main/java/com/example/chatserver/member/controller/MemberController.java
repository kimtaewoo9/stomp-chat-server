package com.example.chatserver.member.controller;

import com.example.chatserver.common.auth.JwtTokenProvider;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.domain.Role;
import com.example.chatserver.member.dto.MemberLoginReqDto;
import com.example.chatserver.member.dto.MemberResponseDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.service.MemberService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final JwtTokenProvider jwtTokenprovider;

    @PostMapping("/member/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto requestDto) {

        Member member = memberService.create(requestDto);
        return ResponseEntity.ok(member.getId());
    }

    @PostMapping("/member/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginReqDto memberLoginReqDto) {
        Member member = memberService.doLogin(memberLoginReqDto);

        // 일치할 경우 access 토큰 발행 후 사용자에게 돌려준다.
        String jwtToken = jwtTokenprovider.createToken(member.getEmail(), Role.USER); // 일단 다 User
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);

        return ResponseEntity.ok(loginInfo);
    }

    @GetMapping("/member/list")
    public ResponseEntity<List<MemberResponseDto>> memberList() {
        List<MemberResponseDto> memberResponseDtos = memberService.getMemberList();
        return ResponseEntity.ok(memberResponseDtos);
    }
}
