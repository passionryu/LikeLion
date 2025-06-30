package me.shinsunyoung.backend.Aop;

import lombok.extern.slf4j.Slf4j;
import me.shinsunyoung.backend.ThreadLocal.TraceIdHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Aspect //곹통으로 관리하고 싶은 기능을 담당하는 클래스에 붙히는 어노테이션
public class LogAspect {

    //PointCut
    //AOP를 적용할 클래스
    @Pointcut("execution(* me.shinsunyoung.backend.Board.Service.BoardService..*(String,String)) ||" +
            "execution(* me.shinsunyoung.backend.OAuth..*(..)) ||" +
            "execution(* me.shinsunyoung.backend.User.Service..*(..))"
                )
    public void method() {
    }


    //@Around 는 호출 시작과 종료 모두에 관려할 수 있는 AOP Advice
    @Around("method()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

//        String traceId = UUID.randomUUID().toString().substring(0, 8);
//        TraceIdHolder.set(traceId);


        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName(); //aop가 실행된 메서드
        try {
            log.info("[AOP_LOG] [TraceId]{} {} 메서드 호출 시작 ", TraceIdHolder.get(), methodName);

            Object result = joinPoint.proceed();   //JoinPoint //AOP를 적용할 시점
            return result;
        } catch (Exception e) {
            log.error("[AOP_LOG] {} 메서드 예외 {} ", methodName, e.getMessage());
            return e;
        } finally {
            long end = System.currentTimeMillis();
            log.info("[AOP_LOG] [TraceId]{} {} 메서드 실행 완료 시간 = {}", TraceIdHolder.get(), methodName, end - start);
            //  TraceIdHolder.clear(); -> 필터 측에서 구현

        }
    }

    // @Before aop가 실행되기 직전에 호출
    @Before("execution(*  me.shinsunyoung.backend.Board.Service..*(..))")
    public void beforeLog(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        log.info("[AOP_LOG][START] → 메서드 = {} 호출 시작", method);
    }


    // @After aop가 실행된 직후에 호출
    @After("execution(*  me.shinsunyoung.backend.Board.Service..*(..))")
    public void afterLog(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        log.info("[AOP_LOG][END] ← 메서드 = {} 호출 종료", method);
    }

}

