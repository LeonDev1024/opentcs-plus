package org.opentcs.map.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.opentcs.map.domain.to.AllowedOperationTO;
import org.opentcs.map.domain.to.AllowedPeripheralOperationTO;
import org.opentcs.map.domain.to.PropertyTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * MyBatis配置类
 */
@Configuration
public class MybatisConfig {

    /**
     * 注册JSONB类型处理器
     * 
     * @return ConfigurationCustomizer
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
            // 注册List类型的类型处理器，专门针对JSONB字段
            registry.register(List.class, JdbcType.OTHER, JacksonTypeHandler.class);
        };
    }
}