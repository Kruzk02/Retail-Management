package org.service.impl;

import lombok.AllArgsConstructor;
import org.dao.EmployeeDao;
import org.dto.LoginRequest;
import org.dto.RegisterRequest;
import org.exception.InvalidValidatorException;
import org.exception.UsernameOrEmailAlreadyExistsException;
import org.model.Employee;
import org.service.EmployeeService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.validators.RegisterRequestValidator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User service class responsible for user related operations such as registration, login and retrieval <p>
 * This class interacts with the UserDao for data access.
 */
@AllArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDao employeeDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RegisterRequestValidator registerRequestValidator;

    /**
     * Register a new user based on the provided RegisterRequest.<p>
     * @param request The RegisterRequest object containing user registration information.
     * @return The registered User entity.
     */
    @Override
    public Employee register(RegisterRequest request) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "registerRequest");
        registerRequestValidator.validate(request, errors);

        if (errors.hasErrors()) {
            List<String> errorMessages = errors.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new InvalidValidatorException(errorMessages);
        }

        if (employeeDao.isUsernameOrEmailExists(request.username(), request.email())) {
            throw new UsernameOrEmailAlreadyExistsException("Username or Email already existing.");
        }

        String hashedPassword = passwordEncoder.encode(request.password());
        return employeeDao.register(Employee.builder()
            .username(request.username())
            .email(request.email())
            .password(hashedPassword)
            .build());
    }

    /**
     * Authentication a user base on provided LoginDTO<p>
     * Use the AuthenticationManager to authentication user and sets the in the SecurityContextHolder.
     *
     * @param request The LoginRequest object containing user login credentials.
     * @return The Authentication user.
     */
    @Override
    public Authentication login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
        } catch (InternalAuthenticationServiceException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
