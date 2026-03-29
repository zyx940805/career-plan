package com.career.plan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.career.plan.mapper")
public class CareerPlanApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareerPlanApplication.class, args);

    }

}
