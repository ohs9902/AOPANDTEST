package com.example.newspeed.controller;

import com.example.newspeed.config.WebSecurityConfig;
import com.example.newspeed.dto.SignUpRequestDto;
import com.example.newspeed.entity.User;
import com.example.newspeed.filter.MockSpringSecurityFilter;
import com.example.newspeed.security.UserDetailsImpl;
import com.example.newspeed.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class UserControllerTest {
    private MockMvc mvc;

    private Principal mockPrincipal;
    @Autowired
    WebApplicationContext context;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService userService;

    @BeforeEach
    public void setUp(){
        mvc = MockMvcBuilders.webAppContextSetup(context).
                apply(springSecurity(new MockSpringSecurityFilter())).
                build();
    }

    public void mockUserSetUp(){
        //mock 테스트 유저 생성
        String userId = "xjr279612@";
        String password = "Xjr8406@123";
        String username = "ddaw12";
        String email = "ohs9902@naver.com";
        String intro = "dawdawda12121";
        User testUser = new User(userId,password,username,email,intro);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails,"",testUserDetails.getAuthorities());
    }
    //회원가입 테스트
    @Test
    public void test1() throws Exception {
        // given
        SignUpRequestDto signupRequest = new SignUpRequestDto();
        signupRequest.setUserId("xjraad279612");
        signupRequest.setPassword("Xjr8406@123");
        signupRequest.setUsername("ddaw12");
        signupRequest.setEmail("ohs9902@naver.com");
        signupRequest.setIntro("dawdawda12121");

        String signupRequestJson = objectMapper.writeValueAsString(signupRequest);

        // when - then
        doNothing().when(userService).singUp(any(SignUpRequestDto.class));

        mvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("회원가입 "));
    }
}