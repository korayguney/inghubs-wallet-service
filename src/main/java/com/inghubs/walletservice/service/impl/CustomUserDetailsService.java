package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.model.dto.CustomUserDetails;
import com.inghubs.walletservice.repository.CustomerRepository;
import com.inghubs.walletservice.repository.EmployeeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final CustomerRepository customerRepository;
  private final EmployeeRepository employeeRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    return customerRepository.findByUsername(username)
        .map(customer -> new CustomUserDetails(
            customer.getId(),
            customer.getUsername(),
            customer.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")),
            "CUSTOMER"
        ))
        .or(() -> employeeRepository.findByUsername(username)
            .map(employee -> new CustomUserDetails(
                employee.getId(),
                employee.getUsername(),
                employee.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")),
                "EMPLOYEE"
            )))
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }
}
