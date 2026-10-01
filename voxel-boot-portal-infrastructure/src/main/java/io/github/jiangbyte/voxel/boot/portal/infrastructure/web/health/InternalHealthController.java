package io.github.jiangbyte.voxel.boot.portal.infrastructure.web.health;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * 内部健康检查 API：供探活与依赖探测（响应形态与 voxel-boot 一致）。
 * <p>放在 infrastructure：依赖 DataSource / RedissonClient。
 *
 * Author: Charlie
 */
@Hidden
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InternalHealthController {

    private final DataSource dataSource;
    private final RedissonClient redissonClient;


    /** 存活探活。 */
    @Operation(summary = "存活探活")
    @GetMapping("/v1/internal/health/live")
    public Map<String, Object> live() {
        return Map.of("status", "live");
    }

    /** 就绪检查。 */
    @Operation(summary = "就绪检查")
    @GetMapping("/v1/internal/health/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        // 1. 依次探测数据库与 Redis
        Map<String, Object> checks = new LinkedHashMap<>();
        boolean databaseOk = checkDatabase(checks);
        boolean redisOk = checkRedis(checks);
        // 2. 汇总就绪状态；任一失败返回 503
        boolean overall = databaseOk && redisOk;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", overall ? "ready" : "not_ready");
        body.put("checks", checks);
        return ResponseEntity.status(overall ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    /**
     * 检查数据库连通性。
     */
    private boolean checkDatabase(Map<String, Object> checks) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("enabled", true);
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute("SELECT 1");
            item.put("ok", true);
            item.put("detail", "connection ok");
            checks.put("database", item);
            return true;
        } catch (SQLException ex) {
            item.put("ok", false);
            item.put("detail", ex.getClass().getSimpleName());
            checks.put("database", item);
            return false;
        }
    }

    /**
     * 检查 Redis 连通性。
     */
    private boolean checkRedis(Map<String, Object> checks) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("enabled", true);
        try {
            redissonClient.getBucket("voxel:health:ready").isExists();
            item.put("ok", true);
            item.put("detail", "connection ok");
            checks.put("redis", item);
            return true;
        } catch (Exception ex) {
            item.put("ok", false);
            item.put("detail", ex.getClass().getSimpleName());
            checks.put("redis", item);
            return false;
        }
    }
}
