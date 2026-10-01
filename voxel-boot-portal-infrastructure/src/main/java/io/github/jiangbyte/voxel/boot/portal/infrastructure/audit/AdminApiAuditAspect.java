package io.github.jiangbyte.voxel.boot.portal.infrastructure.audit;

import io.github.jiangbyte.voxel.boot.portal.domain.auth.adapter.port.SessionPort;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.adapter.port.AuditWriteService;
import io.github.jiangbyte.voxel.boot.portal.domain.sys.audit.OperationAuditLog;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import lombok.RequiredArgsConstructor;

/**
 * 最小管理端写操作审计切面：成功完成的 admin API POST 写入 sys_operation_audit_log。
 *
 * Author: Charlie
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AdminApiAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AdminApiAuditAspect.class);

    private final AuditWriteService auditWriteService;
    private final SessionPort sessionPort;


    /**
     * 环绕管理端 POST 接口：成功后落审计。
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController) && execution(* io.github.jiangbyte.voxel.boot.portal.trigger..*(..))")
    public Object aroundAdminPost(ProceedingJoinPoint pjp) throws Throwable {
        // 1. 仅拦截 HTTP POST 的 admin 写路径
        HttpServletRequest request = currentRequest();
        if (request == null || !"POST".equalsIgnoreCase(request.getMethod())) {
            return pjp.proceed();
        }
        String uri = request.getRequestURI();
        if (uri == null || !uri.contains("/v1/admin/")) {
            return pjp.proceed();
        }
        // 2. 跳过审计自身与健康检查等噪音路径
        if (uri.contains("/sys/audit/") || uri.contains("/internal/")) {
            return pjp.proceed();
        }

        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        // 3. 业务正常返回后写审计（异常向上抛出不记成功）
        try {
            String accountId = sessionPort.currentAdminAccountId();
            String action = extractAction(uri);
            String resourceType = extractResourceType(uri);
            String module = extractModule(uri);
            OperationAuditLog auditLog = OperationAuditLog.recordSuccess(
                    module,
                    resourceType,
                    null,
                    action,
                    action,
                    mapActionType(action),
                    module,
                    buildSummary(module, resourceType, action, accountId),
                    accountId,
                    "ADMIN",
                    accountId,
                    request.getHeader("X-Request-Id"),
                    clientIp(request),
                    request.getHeader("User-Agent"),
                    (int) Math.min(Integer.MAX_VALUE, System.currentTimeMillis() - start));
            auditWriteService.append(auditLog);
        } catch (Exception ex) {
            log.warn("Failed to append operation audit log: {}", ex.getMessage());
        }
        return result;
    }

    private static HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? null : attrs.getRequest();
    }

    private static String extractAction(String uri) {
        String[] parts = uri.split("/");
        return parts.length == 0 ? "unknown" : parts[parts.length - 1];
    }

    private static String extractResourceType(String uri) {
        // /api/v1/admin/{module}/{resource}/...
        String[] parts = uri.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if ("admin".equals(parts[i]) && i + 2 < parts.length) {
                return parts[i + 1] + "_" + parts[i + 2];
            }
        }
        return "unknown";
    }

    private static String extractModule(String uri) {
        String[] parts = uri.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("admin".equals(parts[i]) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return "unknown";
    }

    private static String mapActionType(String action) {
        if (action == null) {
            return "OTHER";
        }
        return switch (action.toLowerCase()) {
            case "create", "submit" -> "CREATE";
            case "update", "enabled", "fields", "update-batch" -> "UPDATE";
            case "delete" -> "DELETE";
            case "run", "download" -> "OTHER";
            default -> "OTHER";
        };
    }

    private static String buildSummary(String module, String resourceType, String action, String accountId) {
        return module + " " + resourceType + " " + action
                + (accountId == null ? "" : (" by " + accountId));
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
