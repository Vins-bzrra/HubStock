package com.vins.hubstock.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vins.hubstock.dto.ResetPasswordDTO;
import com.vins.hubstock.dto.UserLoginDTO;
import com.vins.hubstock.dto.UserRegisterDTO;
import com.vins.hubstock.entity.UserHistory;
import com.vins.hubstock.entity.UserRole;
import com.vins.hubstock.entity.Users;
import com.vins.hubstock.repository.UserHistoryRepository;
import com.vins.hubstock.repository.UsersRepository;
import com.vins.hubstock.security.filter.AuthenticateFilter;
import com.vins.hubstock.security.filter.TokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class UsersService {
    private UsersRepository usersRepository;
    private UserHistoryRepository historyRepository;
    private PasswordEncoder passwordEncoder;

    public String loginUser(UserLoginDTO login) throws Exception {
        Users user = usersRepository.findByRegistrationNumber(login.getRegistrationNumber()).orElseThrow(() -> new Exception("Usuário não encontrado"));
        if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            throw new Exception("Senha inválida");
        }

        String token = JWT.create().withSubject(user.getId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + AuthenticateFilter.TOKEN_EXPIRACAO))
                .sign(Algorithm.HMAC512(AuthenticateFilter.TOKEN_SENHA));

        return token;
    }

    public void registerUser(UserRegisterDTO register) {
        try {
            String name = register.getName();
            String lastName = register.getLastName();
            String registartionNumber = register.getRegistrationNumber();
            String password = register.getPassword();
            UserRole role = UserRole.valueOf(register.getUserRole());

            String encodedPassword = passwordEncoder.encode(password);
            Users user = new Users();
            user.setName(name);
            user.setLastName(lastName);
            user.setRegistrationNumber(registartionNumber);
            user.setPassword(encodedPassword);
            user.setUserRole(role);

            usersRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException("Falha ao registrar o usuário", e);
        }
    }

    public List<Users> searchUsers() {
        return usersRepository.findAll();
    }

    @Transactional
    public HttpStatus deleteUser(long id, Users responsibleUser) {
        try {
            Users user = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            UserHistory history = new UserHistory();
            history.setNameUserRemoved(user.getName());
            history.setLastNameUserRemoved(user.getLastName());
            history.setRegistrationNumberUserRemoved(user.getRegistrationNumber());
            history.setRegistrationNumberResponsibleUser(responsibleUser.getRegistrationNumber());
            history.setChangeDateTime(LocalDateTime.now());
            historyRepository.save(history);
            usersRepository.delete(user);
            return HttpStatus.OK;
        } catch (RuntimeException e) {
            return HttpStatus.NOT_FOUND;
        }
        catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    public HttpStatus resetPassword(ResetPasswordDTO reset) {
        try {
            Users user = usersRepository.findById(Long.parseLong(reset.getIdUser())).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            String encondedPassword = passwordEncoder.encode(reset.getNewPassword());
            user.setPassword(encondedPassword);
            usersRepository.save(user);
            return HttpStatus.OK;
        } catch (RuntimeException e) {
            return HttpStatus.NOT_FOUND;
        }catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

    }

    public String searchUser(String registrationNumber) {
        try {
            Users user = usersRepository.findByRegistrationNumber(registrationNumber).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            return user.getName() + " " + user.getLastName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Users getUserFromToken(String token) throws JWTVerificationException {
        try {
            token = token.replace(TokenFilter.ATRIBUTO_PREFIXO, "");
            Algorithm algorithm = Algorithm.HMAC512(AuthenticateFilter.TOKEN_SENHA);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            Long userId = Long.parseLong(jwt.getSubject());
            Users user = usersRepository.findById(userId).orElse(null);
            return user;
        } catch (JWTVerificationException e) {
            throw new IllegalArgumentException("Token inválido");
        }
    }
}
