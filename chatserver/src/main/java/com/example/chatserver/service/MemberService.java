package com.example.chatserver.service;

import com.example.chatserver.domain.Member;
import com.example.chatserver.dto.MemberSaveReqDto;
import com.example.chatserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member create(MemberSaveReqDto requestDto) {

        String name = requestDto.getName();
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        if(memberRepository.existsByEmail(email)){
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        Member newMember = Member.builder()
            .name(name)
            .email(email)
            .password(password)
            .build();
        Member savedMember = memberRepository.save(newMember);

        return savedMember;
    }
}
