package com.example.OpenSchoolT1.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

@Component
@Aspect
public class LoggingAspect {

	@Before("@annotation(com.example.OpenSchoolT1.annotation.Loggable)")
	public void logBefore(JoinPoint joinPoint) {
		Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
		logger.info("Before: {}.{}() with args = {}",
				joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(),
				joinPoint.getArgs());
	}

	@AfterThrowing(
			pointcut = "execution(* com.example.OpenSchoolT1.service.TaskService.*(..))",
			throwing = "ex"
	)
	public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
		Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
		logger.error("AfterThrowing in {}.{}() with exception = {}",
				joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(),
				ex.getMessage());

	}

	@AfterReturning(
			pointcut = "@annotation(com.example.OpenSchoolT1.annotation.Loggable)",
			returning = "result"
	)
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
		Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
		logger.info("AfterReturning: from {}.{}() with result = {}",
				joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(),
				result);
	}

	@Around("@annotation(com.example.OpenSchoolT1.annotation.MeasureTime)")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
		long start = System.currentTimeMillis();

		try {
			Object result = joinPoint.proceed();
			long executionTime = System.currentTimeMillis() - start;

			logger.info("Around: {}.{}() executed in {} ms",
					joinPoint.getSignature().getDeclaringTypeName(),
					joinPoint.getSignature().getName(),
					executionTime);

			return result;
		} catch (Exception e) {
			long executionTime = System.currentTimeMillis() - start;
			logger.error("Around: {}.{}() failed after {} ms with exception = {}",
					joinPoint.getSignature().getDeclaringTypeName(),
					joinPoint.getSignature().getName(),
					executionTime,
					e.getMessage());
			throw e;
		}
	}
}
