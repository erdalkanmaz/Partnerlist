package com.partnerlist.config;

import com.partnerlist.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    
    @Autowired
    private UserService userService;
    
    @PostConstruct
    public void init() {
        userService.createDefaultAdmin();
    }
}
