package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.model.dto.CustomUserDetails;
import com.inghubs.walletservice.model.entity.Customer;
import com.inghubs.walletservice.model.entity.Employee;
import com.inghubs.walletservice.repository.CustomerRepository;
import com.inghubs.walletservice.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    private final CustomUserDetailsService userDetailsService = new CustomUserDetailsService(customerRepository, employeeRepository);

    @Test
    @DisplayName("loadUserByUsername returns customer details when customer exists")
    void loadUserByUsernameReturnsCustomerDetailsWhenCustomerExists() {
        // given
        String username = "customer1";
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUsername(username);
        customer.setPassword("password");
        when(customerRepository.findByUsername(username)).thenReturn(Optional.of(customer));

        // when
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        // then
        assertEquals(1L, userDetails.getId());
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals("ROLE_CUSTOMER", userDetails.getAuthorities().iterator().next().getAuthority());
        verify(customerRepository, times(1)).findByUsername(username);
        verify(employeeRepository, never()).findByUsername(username);
    }

    @Test
    @DisplayName("loadUserByUsername returns employee details when employee exists")
    void loadUserByUsernameReturnsEmployeeDetailsWhenEmployeeExists() {
        // given
        String username = "employee1";
        Employee employee = new Employee();
        employee.setId(2L);
        employee.setUsername(username);
        employee.setPassword("password");
        when(customerRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.of(employee));

        // when
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        // then
        assertEquals(2L, userDetails.getId());
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals("ROLE_EMPLOYEE", userDetails.getAuthorities().iterator().next().getAuthority());
        verify(employeeRepository, times(1)).findByUsername(username);
        verify(customerRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("loadUserByUsername throws UsernameNotFoundException when user does not exist")
    void loadUserByUsernameThrowsExceptionWhenUserDoesNotExist() {
        // given
        String username = "nonexistent";
        when(customerRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(employeeRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));

        // then
        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(customerRepository, times(1)).findByUsername(username);
        verify(employeeRepository, times(1)).findByUsername(username);
    }
}