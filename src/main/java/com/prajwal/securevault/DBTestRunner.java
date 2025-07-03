//package com.prajwal.securevault;
//
//import com.prajwal.securevault.entity.User;
//import com.prajwal.securevault.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DBTestRunner implements CommandLineRunner {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        User user = new User();
//        user.setUsername("prajwal");
//        user.setPassword(passwordEncoder.encode("secret123"));
//        userRepository.save(user);
//    }
//}
