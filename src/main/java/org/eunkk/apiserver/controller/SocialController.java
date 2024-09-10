package org.eunkk.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.dto.MemberDTO;
import org.eunkk.apiserver.dto.MemberModifyDTO;
import org.eunkk.apiserver.service.MemberService;
import org.eunkk.apiserver.utill.JWTUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
// 나중에 소셜 로그인 증가할 수 있으므로
public class SocialController {

    private final MemberService memberService;

    @GetMapping("/api/member/kakao")
    public Map<String, Object> getMemberFromKakao(String accessToken) {

        log.info("accessToken: " + accessToken);
        MemberDTO memberDTO = memberService.getKakaoMember(accessToken);

        //토큰 기반으로 payload에 들어있는 claims의 정보에 대해 접근할 수 복호화 메서드 호출
        Map<String, Object> claims = memberDTO.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims, 10);
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60*24);

        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }

    @PutMapping("/api/member/modify")
    public Map<String, String> modify (@RequestBody MemberModifyDTO memberModifyDTO) {
        log.info("member modify-----------------" + memberModifyDTO);

        memberService.modifyMember(memberModifyDTO);

        return Map.of("result", "modified");
    }
}
