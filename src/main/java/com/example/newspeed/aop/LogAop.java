package com.example.newspeed.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j(topic="Log AOP")
public class LogAop {
    @Pointcut("execution(* com.example.newspeed.controller.CommentController.*(..))")
    private void comment(){}
    @Pointcut("execution(* com.example.newspeed.controller.ContentController.*(..))")
    private void content(){}
    @Pointcut("execution(* com.example.newspeed.controller.LikeController.*(..))")
    private void like(){}

    @Pointcut("execution(* com.example.newspeed.controller.ProfileController.*(..))")
    private void profile(){}

    @Pointcut("execution(* com.example.newspeed.controller.UserController.*(..))")
    private void user(){}
    @Around("comment()||content()||like()||profile()||user()")
    public Object logHttpRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        //HTTP 요청 정보 가져오기
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();

        //Http 메소드와 URL 로깅
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        log.info("HTTP Method : " + method);
        log.info("Request URL : " + url);

        Object result = joinPoint.proceed();
        return result;
    }
}
