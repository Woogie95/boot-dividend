package com.example.dividend.controller;

import com.example.dividend.model.Auth;
import com.example.dividend.model.MemberEntity;
import com.example.dividend.security.TokenProvider;
import com.example.dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        MemberEntity register = this.memberService.register(request);
        return ResponseEntity.ok(register);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        MemberEntity memberEntity = this.memberService.authenticate(request);
        log.info("user login -> " + request.getUsername());
        return ResponseEntity.ok(
                this.tokenProvider.generateToken(memberEntity.getUsername(), memberEntity.getRoles()));
    }

}
