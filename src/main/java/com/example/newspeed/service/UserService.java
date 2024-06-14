package com.example.newspeed.service;

import com.example.newspeed.dto.LoginRequestDto;
import com.example.newspeed.dto.SignUpRequestDto;
import com.example.newspeed.entity.User;
import com.example.newspeed.jwt.JwtUtil;
import com.example.newspeed.jwt.LogoutFilter;
import com.example.newspeed.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final LogoutFilter logoutFilter;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository userRepository, LogoutFilter logoutFilter, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.logoutFilter = logoutFilter;
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public void singUp(SignUpRequestDto signUpRequestDto){
        String userId = signUpRequestDto.getUserId();
        String password = passwordEncoder.encode(signUpRequestDto.getPassword());
        String name = signUpRequestDto.getUsername();
        String email = signUpRequestDto.getEmail();
        String intro = signUpRequestDto.getIntro();
        //String status = signUpRequestDto.getStatus();
        Optional<User> checkUserId = userRepository.findByUserId(userId);
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if(checkUserId.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 id 입니다.");
        } else if (userId.length()<10 || userId.length()>20) {
            throw new IllegalArgumentException("ID는 10에서20자 이내로 만 가능합니다.");
        } else if (!userId.matches("^[a-zA-Z0-9]*$")) {
            throw new IllegalArgumentException("대소문자 영어와 숫자만 가능합니다.");
        } else if (password.length()<10){
            throw new IllegalArgumentException("비밀번호는 10자 이상만 가능 합니다.");
        }
        if(checkEmail.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 email 입니다.");
        }
        

        User user = new User(userId,password,name,email,intro);
        userRepository.save(user);
    }
    @Transactional
    public void withdrawal(LoginRequestDto loginRequestDto, HttpServletResponse res) throws ServletException, IOException { //회원삭제에 필요한 필드와 로그인에 필요한 필드가 동일함으로 dto재사용
        System.out.println("회원탈퇴 서비스 진입");
       Optional<User> optionalUser = userRepository.findByUserId(loginRequestDto.getUserId());
       if (optionalUser.isPresent()){
           User user = optionalUser.get();
           if(passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword())){
               user.withdhrawnStatus();
               user.updateToken("");
               userRepository.save(user);
               SecurityContextHolder.clearContext(); // 현재 사용자의 인증 정보를 제거
               JwtUtil jwtUtil = new JwtUtil();
               //헤더에서 토큰 제거
               res.setHeader(jwtUtil.AUTHORIZATION_HEADER,"");

           }else{
               throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
           }
       }else{
           throw new UsernameNotFoundException("사용자를 찾을 수 없거나 비밀번호가 일치하지 않습니다.");
       }
    }

    @Transactional
    public void updateRefreshToken(String userId,String refreshToekn){
        User user = userRepository.findByUserId(userId).orElseThrow(()->new UsernameNotFoundException("존재하지 않는 유저입니다."));
        user.updateToken(refreshToekn);
        userRepository.save(user);
    }



}
