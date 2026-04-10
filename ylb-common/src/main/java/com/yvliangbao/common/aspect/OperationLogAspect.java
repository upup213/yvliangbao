package com.yvliangbao.common.aspect;
import com.alibaba.fastjson.JSON;
import com.yvliangbao.common.annotation.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志AOP切面
 * 自动拦截带有@OperationLog注解的方法并记录日志
 *
 * @author 余量宝
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private AdminOperationLogService adminOperationLogService;

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.yvliangbao.common.annotation.OperationLog)")
    public void logPointCut() {
    }

    /**
     * 方法执行后记录日志
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 方法异常时记录日志
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    /**
     * 处理日志记录
     */
    protected void handleLog(JoinPoint joinPoint, Exception e, Object jsonResult) {
        try {
            // 获得注解
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog operationLog = method.getAnnotation(OperationLog.class);
            if (operationLog == null) {
                return;
            }

            // 获取当前用户
            Long adminId = SecurityUtil.getCurrentUserId();
            String adminName = SecurityUtil.getCurrentUsername();
            if (adminId == null) {
                // 未登录用户不记录日志
                return;
            }

            // 获取请求信息
            HttpServletRequest request = getRequest();
            String ip = getClientIp(request);

            // 构建日志内容
            String content = buildContent(joinPoint, operationLog, jsonResult);

            // 记录日志
            adminOperationLogService.recordLog(
                adminId,
                adminName,
                operationLog.type().getDescription(),
                operationLog.module(),
                content,
                ip,
                e == null ? 1 : 0,
                e != null ? e.getMessage() : null
            );

        } catch (Exception ex) {
            log.error("记录操作日志异常: {}", ex.getMessage(), ex);
        }
    }

    /**
     * 构建日志内容
     */
    private String buildContent(JoinPoint joinPoint, OperationLog operationLog, Object jsonResult) {
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("description", operationLog.description());
        
        // 请求参数
        if (operationLog.saveRequestData()) {
            String requestParams = getRequestParams(joinPoint);
            if (requestParams != null && !requestParams.isEmpty()) {
                contentMap.put("requestParams", requestParams);
            }
        }
        
        // 响应结果
        if (operationLog.saveResponseData() && jsonResult != null) {
            try {
                contentMap.put("responseResult", JSON.toJSONString(jsonResult));
            } catch (Exception e) {
                log.warn("序列化响应结果失败: {}", e.getMessage());
            }
        }
        
        try {
            return JSON.toJSONString(contentMap);
        } catch (Exception e) {
            return operationLog.description();
        }
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(JoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] paramValues = joinPoint.getArgs();
            
            if (paramNames == null || paramNames.length == 0) {
                return null;
            }
            
            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                // 过滤掉HttpServletRequest、HttpServletResponse、MultipartFile等
                Object value = paramValues[i];
                if (value instanceof HttpServletRequest 
                    || value instanceof HttpServletResponse 
                    || value instanceof MultipartFile) {
                    continue;
                }
                params.put(paramNames[i], value);
            }
            
            return JSON.toJSONString(params);
        } catch (Exception e) {
            log.warn("获取请求参数失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取请求对象
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 对于通过多个代理的情况，第一个IP才是客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
