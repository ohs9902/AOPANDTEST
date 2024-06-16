package com.example.newspeed.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SignUpRequestDtoTest {
    Validator validator;
    @BeforeEach
    public void setUp(){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Test
    public void userIdSizeTest(){
        //given
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setUserId("abcx");
        dto.setPassword("Abcdca124411");
        dto.setUsername("ValidUser");
        dto.setEmail("invalid@email");
        dto.setIntro("This is a valid intro.");

        //when
        Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(dto);
        ConstraintViolation<SignUpRequestDto> violation = violations.iterator().next();

        //then
        assertEquals("10에서20자 이내로 만 가능합니다.", violation.getMessage());
    }

    @Test
    public void passwordTest(){
        //given
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setUserId("abcxaw21313");
        dto.setPassword("Abcdca124411");
        dto.setUsername("ValidUser");
        dto.setEmail("invalid@email");
        dto.setIntro("This is a valid intro.");

        //when
        Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(dto);
        ConstraintViolation<SignUpRequestDto> violation = violations.iterator().next();

        //then
        assertEquals("대소문자 포함 영문, 숫자, 특수문자를 포함하여 10자 이상 입력해주세요.", violation.getMessage());
    }
    @Test
    public void emailTest(){
        //given
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setUserId("abcxaw21313");
        dto.setPassword("Abcdca124411@");
        dto.setUsername("ValidUser");
        dto.setEmail("invalidemail");
        dto.setIntro("This is a valid intro.");
        //when
        Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(dto);
        ConstraintViolation<SignUpRequestDto> violation = violations.iterator().next();
        //then
        assertEquals("이메일 형식이 아닙니다.",violation.getMessage());

    }

    public void introTest(){
        //given
        SignUpRequestDto dto = new SignUpRequestDto();
        dto.setUserId("abcxaw21313");
        dto.setPassword("Abcdca124411@");
        dto.setUsername("ValidUser");
        dto.setEmail("invalid@email");
        dto.setIntro("x".repeat(256));

        //when
        Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(dto);
        ConstraintViolation<SignUpRequestDto> violation = violations.iterator().next();

        //then
        assertEquals("255자 이내로만 작성 가능합니다.", violation.getMessage());
    }
}