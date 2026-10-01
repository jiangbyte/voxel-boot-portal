# Voxel Boot Portal

![JDK](https://img.shields.io/badge/JDK-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supported-4169E1?logo=postgresql&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-Supported-4479A1?logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Supported-DC382D?logo=redis&logoColor=white)
![License](https://img.shields.io/badge/License-Apache_2.0-blue)
![Version](https://img.shields.io/badge/version-1.1.0--SNAPSHOT-orange)

**Voxel Boot Portal** 是面向门户端的 Spring Boot 后端：DDD 七层拆分，仅提供 **Portal** API（`/api/v1/portal/**`），覆盖认证、个人中心、公告与站点公开能力。

## 目录

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [工程结构](#工程结构)
- [快速开始](#快速开始)
- [默认账号](#默认账号)
- [License](#license)

## 功能特性

API 前缀为 `/api/v1/portal/*`（含部分 public），常见能力如下：

| 模块 | 说明 |
| --- | --- |
| 账号体系 | PORTAL 会话（Sa-Token）；登录 / 注册 / 找回密码；密码 RSA 传输、验证码、失败锁定；可配置三方登录 |
| 门户内容 | Banner、公告列表与详情、意见反馈 |
| 个人中心 | 资料、头像、实名认证、消息、登录日志、安全设置（密码 / 手机 / 邮箱 / 三方绑定 / 注销） |
| 对象存储 | S3 兼容存储，直链或预签名访问 |
| 站点信息 | 页脚版权与备案等公开配置 |

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | JDK 21 · Spring Boot 4.1 · Maven 多模块 · 虚拟线程 |
| 持久化 | PostgreSQL / MySQL · MyBatis-Plus · Dynamic Datasource |
| 缓存 / 会话 | Redis · Redisson · Sa-Token |
| 文档 | Knife4j / SpringDoc |
| 其他 | JustAuth · AWS SDK v2（S3）· Hutool · MapStruct |

## 工程结构

```text
voxel-boot-portal/
├── voxel-boot-portal-types/
├── voxel-boot-portal-api/
├── voxel-boot-portal-domain/
├── voxel-boot-portal-infrastructure/
├── voxel-boot-portal-case/
├── voxel-boot-portal-trigger/
├── voxel-boot-portal-app/
└── scripts/
```

依赖方向：`Trigger → API → Case → Domain ← Infrastructure`；`App` 依赖全部。包根：`io.github.jiangbyte.voxel.boot.portal`。

| 文件 | 用途 |
| --- | --- |
| `scripts/voxel_boot.sql` | MySQL 全量建表、种子数据与表/列 `COMMENT` |
| `scripts/docker/docker-compose.yml` | 中间件 + 应用 Compose |
| `scripts/docker/.env.example` | 部署环境变量模板（复制为 `.env`，勿提交） |

## 快速开始

### 环境要求

- JDK **21**、Maven **3.9+**
- MySQL 8+、Redis

### 1. 初始化数据库

```bash
mysql -u root -p -e "CREATE DATABASE voxel_boot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p voxel_boot < scripts/voxel_boot.sql
```

开发配置见 [`voxel-boot-portal-app/src/main/resources/application-dev.yml`](voxel-boot-portal-app/src/main/resources/application-dev.yml)。`VOXEL_CONFIG_CRYPTO_KEY` 须与种子密文匹配。

### 2. 启动后端

```bash
mvn -pl voxel-boot-portal-app -am spring-boot:run
# 或
mvn -pl voxel-boot-portal-app -am package -DskipTests
java -jar voxel-boot-portal-app/target/voxel-boot-portal-app-*.jar
```

| 项 | 地址 |
| --- | --- |
| API | http://127.0.0.1:8001 |
| 接口文档（Knife4j） | http://127.0.0.1:8001/doc.html |
| OpenAPI JSON | http://127.0.0.1:8001/v3/api-docs |

默认端口 `8001`。

## 默认账号

| 端 | 地址 | 账号 | 密码 | 说明 |
| --- | --- | --- | --- | --- |
| Portal | http://127.0.0.1:8001 | `user` | `123456` | 门户演示账号 |

仅供本地演示。部署后请修改默认密码与敏感配置。更多种子见 `scripts/voxel_boot.sql`。

## License

本项目基于 Apache License 2.0 开源。
