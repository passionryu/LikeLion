package me.shinsunyoung.backend.ThreadLocal;

//  이 클래스는 스레드마다 고유한 요청 ID를 저장하고 꺼내는 역할을 한다.
public class TraceIdHolder {

    // 스레드 ID : 하나의 요청을 고유하게 식별하는 ID

    // Static이기에 딱 하나만 올라감
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // ThreadLocal에 값을 저장하고 거낼 set 메서드와 get 메서드
    public static void set(String traceId){threadLocal.set(traceId);}
    public static String get(){return threadLocal.get();}

    //하나의 요청이 끝났을 때, ThreadLocal에 저장된 값을 지우기 위한 clear 메서드 - 메모리 사용량 절약
    public static void clear(){
        threadLocal.remove();
    }



}
