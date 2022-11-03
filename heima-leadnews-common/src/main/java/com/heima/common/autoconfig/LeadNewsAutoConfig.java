package com.heima.common.autoconfig;

import com.heima.common.autoconfig.properties.MinIOProperties;
import com.heima.common.autoconfig.template.MinIOTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@EnableConfigurationProperties({MinIOProperties.class})
public class LeadNewsAutoConfig {

    @Bean
    //如果配置文件中有  minio.enable属性，且值为true，代表该方法会执行，否则不会执行
    @ConditionalOnProperty(prefix = "minio", value = "enable", havingValue = "true")
    public MinIOTemplate minIOTemplate(MinIOProperties minIOProperties) {
        return new MinIOTemplate( minIOProperties);
    }
}