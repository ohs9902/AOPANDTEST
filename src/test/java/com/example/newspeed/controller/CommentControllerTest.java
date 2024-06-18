package com.example.newspeed.controller;

import com.example.newspeed.config.WebSecurityConfig;
import com.example.newspeed.dto.CommentGetResponse;
import com.example.newspeed.dto.CommentRequest;
import com.example.newspeed.entity.User;
import com.example.newspeed.filter.MockSpringSecurityFilter;
import com.example.newspeed.security.UserDetailsImpl;
import com.example.newspeed.service.CommentService;
import com.example.newspeed.service.LikeService;
import com.example.newspeed.service.ProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {CommentController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class CommentControllerTest {
    private MockMvc mvc;

    private Principal mockPrincipal;
    @Autowired
    WebApplicationContext context;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ProfileService profileService;

    @MockBean
    CommentService commentService;

    private UserDetailsImpl mockUserDetails;

    @BeforeEach
    public void setUp(){
        mockUserDetails = new UserDetailsImpl(mockUserSetUp());
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();


        mockPrincipal = new UsernamePasswordAuthenticationToken(mockUserDetails,"",mockUserDetails.getAuthorities());
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
    @DisplayName("댓글 달기 테스트")
    public void testAddComment() throws Exception {
        //given
        Long id = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setComment("테스트 댓글 달기");

        when(commentService.create(eq(id),any(CommentRequest.class),any(UserDetailsImpl.class)))
                .thenReturn(1L);

        //when - then
        mvc.perform(post("/api/content/{id}/comment",id)
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest))
        ).andExpect(status().isOk());
    }
    @Test
    @DisplayName("댓글 조회 테스트")
    public void testGetComment() throws Exception {
        //given
        Long commentId = 1L;
        CommentGetResponse mockResponse = new CommentGetResponse(commentId,mockUserDetails.getUser().getId(), "This is a test comment");

        when(commentService.get(eq(commentId))).thenReturn(mockResponse);
        //when - then
        mvc.perform(get("/api/comment/{comment_id}",commentId)
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
         .andExpect(jsonPath("$.id").value(commentId))
         .andExpect(jsonPath("$.comment").value("This is a test comment"));
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    public void testUpdateComment() throws Exception {
        //given
        Long id = 1L;
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setComment("new comment");

        when(commentService.update(eq(id),any(CommentRequest.class),any(UserDetailsImpl.class))).thenReturn(id);
        //when - then
        mvc.perform(put("/api/comment/{comment_id}",id)
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest))
        ).andExpect(status().isOk());
    }

}