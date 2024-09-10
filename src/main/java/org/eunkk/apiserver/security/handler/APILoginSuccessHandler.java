package org.eunkk.apiserver.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.dto.MemberDTO;
import org.eunkk.apiserver.utill.JWTUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 로그인하면 front에게 json 으로 문자열 전달
        log.info("-----------------------");
        log.info(authentication);
        log.info("-----------------------");

        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();

        Map<String, Object> claims = memberDTO.getClaims();

        String accessToken = JWTUtil.generateToken(claims, 10); // 10min만 유효
        String refreshToken = JWTUtil.generateToken(claims, 60*24); // 1day 내내 유효함

        claims.put("accessToken", accessToken); // 입장권 유효시간 최대한 짧게 -> but 만료 시 재로그인 필요
        claims.put("refreshToken", refreshToken); // 교환권 -> A + R 둘다 받아서 A가 만료되면 새롭게 갱신해서 보내줌

        Gson gson = new Gson();

        String jsonStr = gson.toJson(claims);

        response.setContentType("application/json; charset=UTF-8");

        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();

    }
}
