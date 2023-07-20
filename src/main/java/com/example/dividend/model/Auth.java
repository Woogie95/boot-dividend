package com.example.dividend.model;

import lombok.*;

import java.util.List;

public class Auth {

    @Data
    public static class SignIn {
        private String username;
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles;

        public MemberEntity toEntity() {
            if (this.username == null || this.password == null || this.roles == null) {
                throw new IllegalArgumentException("username, password, and roles must be initialized");
            }

            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }

}
