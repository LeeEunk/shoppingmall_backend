package org.eunkk.apiserver.utill;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;


import java.time.ZonedDateTime;
import java.util.*;


import javax.crypto.SecretKey;


@Log4j2
public class JWTUtil {


    private static String key = "1234567890123456789012345678901234567890";


    public static String generateToken(Map<String, Object> valueMap, int min) {
        // jwt 문자열로 변환, 유효시간 필요, access token으로 검증
        // jwt 문자열 체크 필요 -> 스피링 웹 시큐리티는 필터를 이용
        
        // 1. 필터 생성 2. 어떤 경로를 통해 들어와서 동작할건지 설정 3. 잘못되면 어떻게 할건지

        SecretKey key = null;

        try{
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
        
        String jwtStr = Jwts.builder()
                .setHeader(Map.of("type","JWT"))
                .setClaims(valueMap)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();


        return jwtStr;
    }


    public static Map<String, Object> validateToken(String token) {


        Map<String, Object> claim = null;

        try{
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));


            claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                    .getBody();

        }catch(MalformedJwtException malformedJwtException){
            throw new CustomJWTException("MalFormed");
        }catch(ExpiredJwtException expiredJwtException){
            throw new CustomJWTException("Expired");
        }catch(InvalidClaimException invalidClaimException){
            throw new CustomJWTException("Invalid");
        }catch(JwtException jwtException){
            throw new CustomJWTException("JWTError");
        }catch(Exception e){
            throw new CustomJWTException("Error");
        }
        return claim;
    }


}
