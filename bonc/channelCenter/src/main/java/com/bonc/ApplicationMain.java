package com.bonc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.bonc.common.datasource.DynamicDataSourceRegister;
import com.bonc.utils.ThreadPoolProperties;


//@EnableTransactionManagement
@SpringBootApplication
@Import(DynamicDataSourceRegister.class)
@ComponentScan(basePackages = "com.bonc")
@ServletComponentScan(basePackages = "com.bonc")
@MapperScan("com.bonc.busi.*.mapper")
@EnableConfigurationProperties({ThreadPoolProperties.class})
@EnableScheduling
public class ApplicationMain  extends SpringBootServletInitializer{

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApplicationMain.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(ApplicationMain.class, args);
	}

}