package cn.migu.unify.ai.config;

public class TraceThreadLocal {

    public static ThreadLocal<TraceInfo> TTL = new ThreadLocal<TraceInfo>();

}
