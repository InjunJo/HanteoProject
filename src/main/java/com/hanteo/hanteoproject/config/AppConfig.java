package com.hanteo.hanteoproject.config;

import com.hanteo.hanteoproject.repository.CategoryInMemoryRepo;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CategoryInMemoryRepo nodeRepository(){

        return CategoryInMemoryRepo.builder()
            .metaData(new HashMap<>())
            .rootTable(new ArrayList<>())
            .branchTable(new ArrayList<>())
            .leafTable(new ArrayList<>())
            .list(new ArrayList<>()).build();
    }

}
