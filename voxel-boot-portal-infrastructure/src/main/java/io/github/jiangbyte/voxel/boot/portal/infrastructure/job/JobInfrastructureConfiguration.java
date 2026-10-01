package io.github.jiangbyte.voxel.boot.portal.infrastructure.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 任务基础设施配置：事务模板。
 *
 * Author: Charlie
 */
@Configuration
public class JobInfrastructureConfiguration {

    /**
     * 提供 TransactionTemplate，供任务执行器在锁外开启短事务写回结果。
     */
    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
