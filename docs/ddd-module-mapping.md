# DDD 七层映射（voxel-boot-portal）

| 层 | 模块 |
| --- | --- |
| types | `voxel-boot-portal-types` |
| api | `voxel-boot-portal-api` |
| domain | `voxel-boot-portal-domain`（`adapter/port|repository`、`model/*`、`service`） |
| infrastructure | `voxel-boot-portal-infrastructure`（`adapter/*`、`dao`、`gateway`、`redis`、`config`） |
| case | `voxel-boot-portal-case` |
| trigger | `voxel-boot-portal-trigger`（`http`/`mq`/`job`） |
| app | `voxel-boot-portal-app` |
