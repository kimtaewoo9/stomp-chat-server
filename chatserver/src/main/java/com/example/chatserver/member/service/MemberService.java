package com.example.chatserver.member.service;

import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberLoginReqDto;
import com.example.chatserver.member.dto.MemberResponseDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public Member create(@RequestBody MemberSaveReqDto requestDto) {
        String name = requestDto.getName();
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        Member newMember = Member.builder()
            .name(name)
            .email(email)
            .password(passwordEncoder.encode(password))
            .build();
        Member savedMember = memberRepository.save(newMember);

        return savedMember;
    }

    public Member doLogin(MemberLoginReqDto loginDto) {
        String loginEmail = loginDto.getEmail();
        String loginPassword = loginDto.getPassword();

        Member member = memberRepository.findByEmail(loginEmail).orElseThrow(
            () -> new EntityNotFoundException("존재하지 않는 이메일 입니다."));
        // 순서가 원시 비밀번호, 인코딩 된 비밀번호 순으로 파라미터를 전달해야함 .
        if (!passwordEncoder.matches(loginPassword, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

    public List<MemberResponseDto> getMemberList() {
        List<Member> members = memberRepository.findAll();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return members.stream()
            .filter(member -> !member.getEmail().equals(email))
            .map(member -> MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .build())
            .toList();
    }
}
