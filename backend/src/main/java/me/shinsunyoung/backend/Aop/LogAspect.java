package me.shinsunyoung.backend.Aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class LogAspect {

    // AOP를 적용할 클래스
    @Pointcut("execution(* me.shinsunyoung.backend.Board.Service..*(..))" +
                "me.shinsunyoung.backend.Board.Controller")
    public void method(){}

    // PointCut

    /**
     * 보드 서비스 안에 있는 모든 메서드에 적용
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* me.shinsunyoung.backend.Board.Service..*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName();

        try{
            log.info("[AOP_LOG] {} 메서드 호출 시작", methodName);

            Object result = joinPoint.proceed();
            return result;
        }
        catch (Exception e){
            log.error("[AOP_LOG] {} 메서드 예외 {}", methodName, e.getMessage());
            return e;
        }
        finally {
            long end = System.currentTimeMillis();
            log.info("[AOP_LOG] {} 메서드 실행 완료 시간 ={}", methodName, end - start);

        }
    }
}
