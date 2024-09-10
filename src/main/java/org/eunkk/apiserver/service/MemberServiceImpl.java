package org.eunkk.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.eunkk.apiserver.domain.Member;
import org.eunkk.apiserver.domain.MemberRole;
import org.eunkk.apiserver.dto.MemberDTO;
import org.eunkk.apiserver.dto.MemberModifyDTO;
import org.eunkk.apiserver.repository.MemberRepository;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO getKakaoMember(String accessToken) {
        
        //accessToken을 이용해서 사용자 정보를 가져오기 -> 카카오 연동
        String email = getEmailFromKakaoAccessToken(accessToken);
        
        // 기존 DB에 회원 정보가 있는 경우 / 없는 경우 -> db처리
        Optional<Member> result = memberRepository.findById(email);

        if(result.isPresent()){ // 기존 회원으로 존재한다면

            MemberDTO memberDTO = entityToDTO(result.get());

            log.info("existed.............." + memberDTO);
            return memberDTO;
            //return
        }
        
        // 존재하지 않는다면 소셜 멤버 등록
        // 닉네임은 'Social Member'
        // 패스워드는 임의로 생성
        Member socialMember = makeSocialMember(email);

        memberRepository.save(socialMember);

        MemberDTO memberDTO = entityToDTO(socialMember);

        return memberDTO;
    }

    @Override
    public void modifyMember(MemberModifyDTO memberModifyDTO) {

        Optional<Member> result = memberRepository.findById(memberModifyDTO.getEmail());
        
        Member member = result.orElseThrow(); //없으면 예외 처리

        member.changeNickname(memberModifyDTO.getNickname());
        member.changeSocial(false);
        member.changePw(passwordEncoder.encode(memberModifyDTO.getPw()));

        log.info("-----------modify member after" + member);

        memberRepository.save(member);
    }

    private Member makeSocialMember(String email) {
        String tempPassword = makeTempPassword();

        log.info("tempPassword: "+ tempPassword);

        Member member = Member.builder()
                .email(email)
                .pw(passwordEncoder.encode(tempPassword))
                .nickname("Social Member")
                .social(true)
                .build();

        member.addRole(MemberRole.USER);

        return member;
    }

    private String getEmailFromKakaoAccessToken(String accessToken){
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type" , "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(uriBuilder.toUri(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info("response............................");
        log.info(response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        log.info("-----------------------------");
        log.info(bodyMap);

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("kakao_account");

        log.info("kakaoAccount: " + kakaoAccount);

        String email = kakaoAccount.get("email");

        log.info("email: " + email);

        return email;

    }

    // 10자리 이하 pw 생성
    private String makeTempPassword() {

        StringBuffer buffer = new StringBuffer();

        for(int i = 0; i<10; i++) {
            buffer.append((char)((int)(Math.random()*55) + 65)); //아스키코드 번호
        }
        return buffer.toString();
    }
}
