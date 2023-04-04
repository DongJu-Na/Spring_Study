package com.springbook.biz.common;

import org.springframework.stereotype.Service;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Service
@Aspect
public class AfterAdvice {
	@Pointcut("execution(* com.springbook.biz..*Impl.*(..))")
	public void allPointcut() {}
	
	@After("allPointcut()")
	public void afterAdvice() {
		System.out.println("[���� ó��] ����Ͻ� ���� ���� �� ������ ����");
	}
}