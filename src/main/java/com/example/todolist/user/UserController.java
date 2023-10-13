package com.example.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;
    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel usuario){

        UserModel user = this.userRepository.findByUsername(usuario.getUsername());

        if(user != null){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
        }

        String passwordHashred = BCrypt.withDefaults().hashToString(12, usuario.getPassword().toCharArray());

        usuario.setPassword(passwordHashred);

        UserModel usuarioCriado =  this.userRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }
}
