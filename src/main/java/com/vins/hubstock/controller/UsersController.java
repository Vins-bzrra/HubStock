package com.vins.hubstock.controller;

import com.vins.hubstock.dto.ResetPasswordDTO;
import com.vins.hubstock.dto.UserLoginDTO;
import com.vins.hubstock.dto.UserRegisterDTO;
import com.vins.hubstock.entity.UserRole;
import com.vins.hubstock.entity.Users;
import com.vins.hubstock.security.BlacklistToken;
import com.vins.hubstock.service.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UsersController {

    private UsersService userService;
    private BlacklistToken tokenBlacklist;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO userLogin) {
        try {
            String token = userService.loginUser(userLogin);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Email ou senha inválidos");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDTO register, @RequestHeader("Authorization") String token) {
        try {
            Users user = userService.getUserFromToken(token);
            if(user.getUserRole() == UserRole.USER || user.getUserRole() == UserRole.OPERATOR) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não possui permissão para essa operação");
            }
            userService.registerUser(register);
            return ResponseEntity.ok(null);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Cadastro não concluído: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestParam("registration") String registration, @RequestHeader("Authorization") String token) {
        try {
            Users user = userService.getUserFromToken(token);
            if(user.getUserRole() == UserRole.USER || user.getUserRole() == UserRole.OPERATOR) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não possui permissão para essa operação");
            }
            String name = userService.searchUser(registration);
            return ResponseEntity.ok(Map.of("name", name));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestHeader("Authorization") String token) {
        try {
            Users user = userService.getUserFromToken(token);
            if(user.getUserRole() == UserRole.USER || user.getUserRole() == UserRole.OPERATOR) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não possui permissão para essa operação");
            }
            List<Users> users = userService.searchUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @PathVariable long id) {
        try {
            Users user = userService.getUserFromToken(token);
            if(user.getUserRole() == UserRole.USER || user.getUserRole() == UserRole.OPERATOR) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não possui permissão para essa operação");
            }
            HttpStatus status = userService.deleteUser(id,user);

            if (status == HttpStatus.OK) {
                return ResponseEntity.status(status).body("Usuário deletado com sucesso");
            } else if(status == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(status).body("Usuário não encontrado");
            }else {
                return ResponseEntity.badRequest().build();
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO reset, @RequestHeader("Authorization") String token){
        try {
            Users user = userService.getUserFromToken(token);
            if(user.getUserRole() == UserRole.USER || user.getUserRole() == UserRole.OPERATOR) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não possui permissão para essa operação");
            }
            HttpStatus status = userService.resetPassword(reset);
            if (status == HttpStatus.OK) {
                return ResponseEntity.status(status).body("Senha alterada com sucesso");
            } else if(status == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(status).body("Usuário não encontrado");
            }else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader("Authorization") String token) {
        try {
            tokenBlacklist.invalidateToken(token);
            if (tokenBlacklist.isTokenInvalidated(token)) {
                return ResponseEntity.ok("Logout realizado com sucesso");
            } else {
                return ResponseEntity.badRequest().body("O token não pôde ser invalidado.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
