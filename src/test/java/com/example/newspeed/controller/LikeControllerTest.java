package com.example.newspeed.controller;

import com.example.newspeed.config.WebSecurityConfig;
import com.example.newspeed.entity.User;
import com.example.newspeed.filter.MockSpringSecurityFilter;
import com.example.newspeed.security.UserDetailsImpl;
import com.example.newspeed.service.LikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
@WebMvcTest(
        controllers = LikeController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class LikeControllerTest {

    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext context;

    private Principal mockPrincipal;
    @MockBean
    private LikeService likeService;

    private UserDetailsImpl mockUserDetails;
    @BeforeEach
    public void setUp(){
        mockUserDetails = new UserDetailsImpl(mockUserSetUp());
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();

        mockPrincipal = new UsernamePasswordAuthenticationToken(
                mockUserDetails,"",mockUserDetails.getAuthorities());

    }
    public User mockUserSetUp(){
        //mock 테스트 유저 생성
        String userId = "xjr279612@";
        String password = "Xjr8406@123";
        String username = "ddaw12";
        String email = "ohs9902@naver.com";
        String intro = "dawdawda12121";
        User testUser = new User(userId,password,username,email,intro);

        return testUser;
    }

    @Test
    @DisplayName("개시물 좋아요 테스트")
    public void contentLikeTest() throws Exception {
        System.out.println("user id :" + mockUserDetails.getUser().getUserId());
        //give
        Long id = 1L;
        when(likeService.contentLike(any(Long.class), any(User.class))).thenReturn(ResponseEntity.ok("좋아요 성공."));

        //when - then
        mvc.perform(MockMvcRequestBuilders.post("/api/like/content/{contentId}",id)
                        .principal(new UsernamePasswordAuthenticationToken(mockUserDetails,""))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("좋아요 성공."));
    }
}