package com.partnerlist.service;

import com.partnerlist.model.User;
import com.partnerlist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public long count() {
        return userRepository.count();
    }
    
    public void createDefaultAdmin() {
        if (count() == 0) {
            User admin = new User();
            admin.setId(1L); // Kimliği elle ata (SQLite ile uyumlu)
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setIsActive(true);
            save(admin);
        }
    }
}
