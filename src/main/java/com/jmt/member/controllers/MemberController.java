package com.jmt.member.controllers;

import com.jmt.global.Utils;
import com.jmt.global.exceptions.BadRequestException;
import com.jmt.global.rests.JSONData;
import com.jmt.member.MemberInfo;
import com.jmt.member.entities.Member;
import com.jmt.member.jwt.TokenProvider;
import com.jmt.member.services.MemberSaveService;
import com.jmt.member.validators.JoinValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class MemberController {

    private final JoinValidator joinValidator;
    private final TokenProvider tokenProvider;
    private final Utils utils;
    private final MemberSaveService saveService;

    //로그인한 회원 정보 조회
    @GetMapping
    public JSONData info(@AuthenticationPrincipal MemberInfo memberInfo) {
        Member member = memberInfo.getMember();
        return new JSONData(member);
    }

    @PostMapping
    public ResponseEntity join(@RequestBody @Valid RequestJoin form, Errors errors) {

        joinValidator.validate(form, errors);

        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        saveService.save(form);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PostMapping("/token")
    public JSONData token(@RequestBody @Valid RequestLogin form, Errors errors) {

        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        String token = tokenProvider.createToken(form.getEmail(), form.getPassword());

        return new JSONData(token);
    }

    @GetMapping("/test1")
    public void memberOnly() {
        log.info("회원전용!");
    }

    @GetMapping("/test2")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void adminOnly() {
        log.info("관리자 전용!");
    }

    private void commonProcess(String mode, Model model) {
        mode = Objects.requireNonNullElse(mode,"");
        List<String> addCss = new ArrayList<>();
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        addCss.add("member/style"); // 회원 공통 스타일

    }

}