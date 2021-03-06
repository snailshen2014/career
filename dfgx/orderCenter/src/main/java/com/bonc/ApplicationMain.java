package com.bonc;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
//schedule
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.Import;
import com.bonc.busi.task.base.SpringUtil;
import com.bonc.common.datasource.DynamicDataSourceRegister;


@EnableTransactionManagement
@SpringBootApplication
@Import({DynamicDataSourceRegister.class,SpringUtil.class})
//@Import(value={SpringUtil.class})
@ComponentScan(basePackages = "com.bonc")
@ServletComponentScan(basePackages = "com.bonc")
@MapperScan("com.bonc.busi.*.mapper")
@EnableAutoConfiguration
@EnableScheduling

public class ApplicationMain extends SpringBootServletInitializer{
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApplicationMain.class);
    }

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ApplicationMain.class);
		//app.addListeners(new ApplicationStartup());
		app.run(args);
	}

}