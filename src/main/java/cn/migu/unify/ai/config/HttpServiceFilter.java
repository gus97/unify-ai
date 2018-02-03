package cn.migu.unify.ai.config;

import cn.migu.unify.ai.utils.DistributedIDS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class HttpServiceFilter implements Filter {

    private Logger traceLog = LoggerFactory.getLogger(HttpServiceFilter.class);

    TraceInfo before;

    TraceInfo after;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        int spanSuffix = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;

        String requestUrl = request.getRequestURI();

        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                ip = ip.substring(0, index);
            }
        } else {
            ip = request.getHeader("X-Real-IP");
            if (ip == null || "unKnown".equalsIgnoreCase(ip)) {
                ip = servletRequest.getRemoteAddr();
            }
        }

        Long id;

        if (null == request.getHeader("MG-Trace-ID")) {
            id = Long.valueOf(new DistributedIDS(0, 0).nextId());
            before = new TraceInfo(id, System.currentTimeMillis() + spanSuffix,
                    null, requestUrl, "SR", System.currentTimeMillis(), ip, null);
        } else {
            id = Long.valueOf(request.getHeader("MG-Trace-ID"));
            before = new TraceInfo(id,
                    System.currentTimeMillis() + spanSuffix, Long.parseLong(request.getHeader("MG-Trace-Span-ID")),
                    requestUrl, "SR", System.currentTimeMillis(), ip, null);
        }

        TraceThreadLocal.TTL.set(before);

        traceLog.info(before.toString());

        try {
            filterChain.doFilter(servletRequest, servletResponse);

            after = new TraceInfo(TraceThreadLocal.TTL.get().traceID, TraceThreadLocal.TTL.get().spanID,
                    TraceThreadLocal.TTL.get().parentID, TraceThreadLocal.TTL.get().url, "SS",
                    System.currentTimeMillis(), ip, null);

            traceLog.info(after.toString());

        } catch (IOException | ServletException e) {

            after = new TraceInfo(TraceThreadLocal.TTL.get().traceID, TraceThreadLocal.TTL.get().spanID,
                    TraceThreadLocal.TTL.get().parentID, TraceThreadLocal.TTL.get().url, "SS",
                    System.currentTimeMillis(), ip, getStackTrace(e));

            traceLog.info(after.toString());
        }
    }

    @Override
    public void destroy() {

    }

    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }
}
