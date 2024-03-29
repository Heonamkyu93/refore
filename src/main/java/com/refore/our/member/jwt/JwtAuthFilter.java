package com.refore.our.member.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.refore.our.member.config.auth.CustomUserDetails;
import com.refore.our.member.dto.JoinDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

// 로그인요청시 post 전송하면 UsernamePasswordAuthenticationFilter 가 동작
// form로그인을 사용안해서 필터를 걸어줘야함
@RequiredArgsConstructor
public class JwtAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Override  //login 요청을 시도하면 로그인 시도를 위해서 실행되는 메소드 id , password 확인후  정상인지 로그인시도
                // authenticationManager 이걸로 로그인시도 하면  userDetailService가 호출되고 loaduserbyusername 메소드 실행
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ObjectMapper om = new ObjectMapper();   // json
            JoinDto joinDto = om.readValue(request.getInputStream(), JoinDto.class);
            // loaduserbyusername 메소드 실행 정상이면 authentication 리턴됨
            // db에 있는 값과 일치
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(joinDto.getMemberEmail(),joinDto.getMemberPassword());
            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);
        
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
        // attempAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 메소드 실행
        // 여기서 jwt 만들고 사용자에게 응답 
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("인증완료");
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        //SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String key = "dkssudgktpdyakssktjqksrkqttmqslekgkgkghgh123testabcasdasdasdwseqasdasdasdasdasdasdasdsadassdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssseasda";


        String refreshToken = Jwts.builder()
                .setSubject("refresh")
                .setExpiration(new Date(System.currentTimeMillis() + (60000 * 60 * 24 * 7))) // 예: 1주일
                .claim("id", customUserDetails.getJoinEntity().getMemberId())
                .signWith(SignatureAlgorithm.HS512, key.getBytes())
                .compact();

        tokenService.saveRefreshToken(customUserDetails.getJoinEntity().getMemberId(), refreshToken);

        String jwt = Jwts.builder()
                .setSubject("access")
                .setExpiration(new Date(System.currentTimeMillis() + (60000 * 10))) // 만료 시간 설정 10분
                .claim("id", customUserDetails.getJoinEntity().getMemberId())
                .claim("email", customUserDetails.getJoinEntity().getMemberEmail())
                .signWith(SignatureAlgorithm.HS512, key.getBytes()) // 알고리즘과 키 지정
                .compact();
        System.out.println("jwt = " + jwt);
        response.addHeader("Authorization","Bearer "+jwt);
        response.addHeader("Refresh-Token", "Bearer " + refreshToken);
    }
}
