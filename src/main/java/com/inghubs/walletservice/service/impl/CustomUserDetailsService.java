package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.model.dto.CustomUserDetails;
import com.inghubs.walletservice.repository.CustomerRepository;
import com.inghubs.walletservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for loading user details in Spring Security.
 * This class retrieves user information from either the Customer or Employee repository.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Loads user details by username.
     * Searches for the user in both Customer and Employee repositories.
     *
     * @param username The username of the user to be loaded.
     * @return UserDetails object containing user information.
     * @throws UsernameNotFoundException If the user is not found in either repository.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findCustomerDetails(username)
                .or(() -> findEmployeeDetails(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Finds user details from the Customer repository.
     *
     * @param username The username of the customer to be retrieved.
     * @return An Optional containing UserDetails if the customer is found, otherwise empty.
     */
    private Optional<UserDetails> findCustomerDetails(String username) {
        return customerRepository.findByUsername(username)
                .map(customer -> new CustomUserDetails(
                        customer.getId(),
                        customer.getUsername(),
                        customer.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")),
                        "CUSTOMER"
                ));
    }

    /**
     * Finds user details from the Employee repository.
     *
     * @param username The username of the employee to be retrieved.
     * @return An Optional containing UserDetails if the employee is found, otherwise empty.
     */
    private Optional<UserDetails> findEmployeeDetails(String username) {
        return employeeRepository.findByUsername(username)
                .map(employee -> new CustomUserDetails(
                        employee.getId(),
                        employee.getUsername(),
                        employee.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")),
                        "EMPLOYEE"
                ));
    }
}