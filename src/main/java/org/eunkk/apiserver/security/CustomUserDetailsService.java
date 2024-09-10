package org.eunkk.apiserver.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.domain.Member;
import org.eunkk.apiserver.dto.MemberDTO;
import org.eunkk.apiserver.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@RequiredArgsConstructor // member repository를 위함
@Service // spring bean 등록
@Log4j2 // log
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    
    // email에 해당하는 값
    // return type과 class type 은 인터페이스로 변경하는 것이 좋음 -> 변경에 용이하기 때문
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("-------loadUserByUsername-------" + username);

        Member member = memberRepository.getWithRoles(username);

        if(member == null) {
            throw new UsernameNotFoundException("Not found");
        }

        MemberDTO memberDTO = new MemberDTO(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList() // 멤버롤을 문자열로 변환
                        .stream()
                        .map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );

        log.info(memberDTO);

        return memberDTO;

    }
}
