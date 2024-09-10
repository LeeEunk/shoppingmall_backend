package org.eunkk.apiserver.security.filter;

import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.dto.MemberDTO;
import org.eunkk.apiserver.utill.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter { // 모든 리퀘스트에 대해 동작


    // jwt이 당연히 없는 login 경로는 확인할 필요가 없음
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // Preflight요청은 체크하지 않음
        if(request.getMethod().equals("OPTIONS")){
            return  true;
        }

        // true = not checking
        String path = request.getRequestURI();

        log.info("check uri ---------------- " + path);

        // api/member/ 경로의 호출은 체크하지 않음
        if(path.startsWith("/api/member/")){
            // true = not checking
            return true;
        }

        if(path.startsWith("/api/todo/")){
            // true = not checking
            return true;
        }

        if(path.startsWith("/api/products/view/")) {
            return true;
        }

        // false == check
        return false; // 모든 경우에 다 체크할 거다라고 가정함
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("---------JWTCheckFilter--------------");

        log.info("-----------------------");

        log.info("-----------------------");

        String authHeaderStr = request.getHeader("Authorization");
        
        //Bearer // 7 JWT문자열
        try {
            //Bearer accesstoken
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);

            log.info("JWT claims: " + claims);

            //destination
            //filterChain.doFilter(request, response); // 아래 추가

            String email = (String) claims.get("email");
            String pw = (String) claims.get("pw");
            String nickname = (String) claims.get("nickname");
            Boolean social = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            MemberDTO memberDTO = new MemberDTO(email, pw, nickname, social.booleanValue(), roleNames);

            log.info("-----------------------------");
            log.info(memberDTO);
            log.info(memberDTO.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(memberDTO, pw, memberDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        }catch (Exception e) {

            log.error("JWT Check Error........................");
            log.error(e.getMessage());

            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }
    }
}
