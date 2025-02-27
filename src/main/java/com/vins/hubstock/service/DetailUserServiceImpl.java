package com.vins.hubstock.service;

import com.vins.hubstock.data.DetailUserData;
import com.vins.hubstock.entity.Users;
import com.vins.hubstock.repository.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class DetailUserServiceImpl implements UserDetailsService {

    private final UsersRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> usuario = repository.findByRegistrationNumber(username);
        if(usuario.isEmpty()) {
            throw new UsernameNotFoundException("Usuário com a matrícula[ " + username + "] não encontrado");
        }
        return new DetailUserData(usuario);
    }
}
