package com.progartisan.module.security.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.progartisan.component.common.BizException;
import com.progartisan.component.framework.AuthInfo;
import com.progartisan.component.framework.Metadata.FunctionDef;

import lombok.Data;

@RequestMapping("/api/security")
public interface SecurityService {

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;

        private String code;
    }

    @PostMapping("/login")
    AuthInfo login(@RequestBody LoginRequest loginRequest);

    @GetMapping("/info")
    AuthInfo getUserInfo(HttpServletRequest request);

    @PostMapping("/logout")
    void logout(HttpServletRequest request);

    @GetMapping("/functions")
    List<FunctionDef> getAllFunctionDefs();

    @GetMapping("/exception")
    void testException() throws BizException;
}
