package com.projects.cloudstorage.security;

import com.projects.cloudstorage.model.User;
import com.projects.cloudstorage.security.jwt.JwtUser;
import com.projects.cloudstorage.security.jwt.JwtUserFactory;
import com.projects.cloudstorage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;


    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userService.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("User " + login + " not found");
        }
        JwtUser jwtUser = JwtUserFactory.create(user);
        log.info("METHOD: loadUserByUsername - user: {} successfully loaded", login);
        return jwtUser;
    }
}
