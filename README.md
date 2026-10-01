# Voxel Boot Portal

门户端 Java 后端（Spring Boot），xfg-ddd 七层：`types` / `api` / `domain` / `infrastructure` / `case` / `trigger` / `app`。

- 包根：`io.github.jiangbyte.voxel.boot.portal`
- 依赖：Trigger → API → Case → Domain ← Infrastructure；App 依赖全部
- 端：仅 **portal**（`/api/v1/portal/**`）
- 默认端口：`8001`

```bash
mvn -pl voxel-boot-portal-app -am package -DskipTests
java -jar voxel-boot-portal-app/target/voxel-boot-portal-app-*.jar
```

演示账号：`user` / `123456`（以库内种子为准）。
