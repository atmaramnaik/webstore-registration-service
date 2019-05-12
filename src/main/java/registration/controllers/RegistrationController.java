package registration.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import registration.dto.RegistrationDTO;
import registration.dto.UserDTO;
import registration.exceptions.InputError;
import registration.model.Role;
import registration.model.User;
import registration.repositories.RoleRepository;
import registration.service.TokenAuthenticationService;
import registration.service.UserManager;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RestController
@RequestMapping(path="/auth")
public class RegistrationController {
    @Autowired
    private UserManager userManager;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @PostMapping(path = "/register",consumes =
            {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ResponseEntity<UserDTO> register(@Valid @RequestBody RegistrationDTO registrationDTO, Errors errors) throws InputError {
        if(errors.hasErrors()){
            throw new InputError(InputError.VALIDATION,errors.getAllErrors().get(0).getDefaultMessage());
        }
        User user=new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(registrationDTO.getPassword());

        UserDTO userDTO=new UserDTO(userManager.createUser(user));
        HttpHeaders responseHeaders=new HttpHeaders();
        responseHeaders.set("authorization","Bearer "+ tokenAuthenticationService.getToken(user));
        ResponseEntity<UserDTO> userDTOResponseEntity=new ResponseEntity<UserDTO>(userDTO,responseHeaders,HttpStatus.CREATED);
        return userDTOResponseEntity;
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path="/all")
    public @ResponseBody Iterable<UserDTO> getAllUsers() {
        return StreamSupport.stream(userManager.getAllUsers().spliterator(),false).map(UserDTO::new).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(path="/roles")
    public @ResponseBody List<String> roles(@RequestHeader("Authorization") String authHeader) {
        return tokenAuthenticationService.getRoles(authHeader);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(path="/profile")
    public @ResponseBody UserDTO profile(Principal principal) {
        return new UserDTO(userManager.findByEmail(principal.getName()));
    }

    @ExceptionHandler(InputError.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody InputError handleException(InputError e) {
        return e;
    }
}
