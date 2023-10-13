package com.msa.member.service;

import com.msa.auth.JwtTokenProvider;
import com.msa.auth.TokenInfo;
import com.msa.member.domain.Member;
import com.msa.member.domain.RefreshToken;
import com.msa.member.repository.MemberRepository;
import com.msa.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public Member addUser(String userName, String email, String password) {
        Member member = new Member(userName, email, passwordEncoder.encode(password),
                Set.of("USER"));
        return memberRepository.save(member);
    }

    @Transactional
    public TokenInfo login(String email, String password) {
        // step 1
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        // step 2
        Authentication authentication = authenticationManagerBuilder
                .getObject().authenticate(authenticationToken);

        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isEmpty()) {
            throw new AccessDeniedException("not found user");
        }
        //step 3
        // accessToken, refreshToken 생성!!
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication, email);

        //step 4
        // refresh token 없으면 생성 or update. 일정 시간 지나면 로그아웃 되므로,
        // 다시 남은 시간 최대로 초기화
        refreshTokenRepository.findByMember_Email(member.get().getEmail())
                .ifPresentOrElse(refreshToken -> {
                    refreshToken.setRefreshToken(tokenInfo.refreshToken());
                    refreshTokenRepository.save(refreshToken);
                }, () -> {
                    refreshTokenRepository.save(new RefreshToken(tokenInfo.refreshToken(), member.get()));
                });

        return tokenInfo;
    }
}
