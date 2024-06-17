package com.example.newspeed.controller;

import com.example.newspeed.config.WebSecurityConfig;
import com.example.newspeed.dto.ProfileRequestDto;
import com.example.newspeed.dto.ProfileResponseDto;
import com.example.newspeed.entity.User;
import com.example.newspeed.filter.MockSpringSecurityFilter;
import com.example.newspeed.security.UserDetailsImpl;
import com.example.newspeed.service.ProfileService;
import com.example.newspeed.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = ProfileController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
@AutoConfigureMockMvc
class ProfileControllerTest {
    private MockMvc mvc;

    private Principal mockPrincipal;
    @Autowired
    WebApplicationContext context;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ProfileService profileService;

    private UserDetailsImpl mockUserDetails;

    @BeforeEach
    public void setUp(){
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    public User mockUserSetUp(){
        //mock 테스트 유저 생성
        String userId = "xjr279612@";
        String password = "Xjr8406@123";
        String username = "ddaw12";
        String email = "ohs9902@naver.com";
        String intro = "dawdawda12121";
        User testUser = new User(userId,password,username,email,intro);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails,"",testUserDetails.getAuthorities());
        return testUser;
    }

    @Test
    @DisplayName("프로필 조회 테스트")
    public void testProfiileInquire() throws Exception {
        //given
        long id = 1L;
        ProfileResponseDto profileResponseDto = new ProfileResponseDto(mockUserSetUp());
        when(profileService.getProfile(id)).thenReturn(profileResponseDto);

        //when - then
        mvc.perform(MockMvcRequestBuilders.get("/api/profile/{id}",id))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value("xjr279612@"))
                .andExpect(jsonPath("$.name").value("ddaw12"))
                .andExpect(jsonPath("$.email").value("ohs9902@naver.com"))
                .andExpect(jsonPath("$.intro").value("dawdawda12121"));

    }

    @Test
    @DisplayName("프로필 업데이트 테스트 ")
    public void testUpdateProfile() throws Exception {
        //given
        ProfileRequestDto requestDto = new ProfileRequestDto();
        ProfileResponseDto responseDto = new ProfileResponseDto(mockUserSetUp());
        when(profileService.update(any(UserDetailsImpl.class),any(ProfileRequestDto.class)))
                .thenReturn(responseDto);

        //when - then
        mvc.perform(put("/api/profile")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value("xjr279612@"))
                .andExpect(jsonPath("$.name").value("ddaw12"))
                .andExpect(jsonPath("$.email").value("ohs9902@naver.com"))
                .andExpect(jsonPath("$.intro").value("dawdawda12121"));
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    public void testPasswordUpdate() throws Exception {
        //given
        ProfileRequestDto requestDto = new ProfileRequestDto();
        requestDto.setPassword("newPassword1234");

        doNothing().when(profileService).updatePassword(any(UserDetailsImpl.class),any(ProfileRequestDto.class));

        //when - then
        mvc.perform(put("/api/profile/password")
                .principal(new UsernamePasswordAuthenticationToken(mockUserDetails,null))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("비밀번호가 변경되었습니다."));
    }

}