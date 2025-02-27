package com.vins.hubstock.controller;


import com.vins.hubstock.dto.ServicePendingDTO;
import com.vins.hubstock.dto.ServiceRequestDTO;
import com.vins.hubstock.entity.UserRole;
import com.vins.hubstock.entity.Users;
import com.vins.hubstock.service.ServiceRequestService;
import com.vins.hubstock.service.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
@AllArgsConstructor
public class ServiceRequestController {

    private ServiceRequestService service;
    private UsersService userService;

    @PostMapping("/request")
    public ResponseEntity<?> ServiceRequest(@RequestBody ServiceRequestDTO serviceRequestDto,
                                            @RequestHeader("Authorization") String token) {
        try {
            Users user = userService.getUserFromToken(token);
            if (user.getUserRole() == UserRole.USER) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuário não possui permissão para essa operação");
            }
            service.createServiceTicket(serviceRequestDto, user);
            return ResponseEntity.ok().body(user.getName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/conclusion")
    public ResponseEntity<?> ServiceConclusion(@RequestParam("ticket") Long id, @RequestHeader("Authorization") String token) {
        try {
            Users user = userService.getUserFromToken(token);
            service.finishServiceTicket(user, id);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> ServicePending(@RequestHeader("Authorization") String token) {
        try{
            Users user = userService.getUserFromToken(token);
            if(user.getUserRole() == UserRole.USER) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não possui permissão para essa operação");
            }
            List<ServicePendingDTO> tickets = service.getServiceTickets(user);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/finished")
    public ResponseEntity<?> ServicesFinished(@RequestHeader("Authorization") String token) {
        try{
            Users user = userService.getUserFromToken(token);
            if(user.getUserRole() == UserRole.USER || user.getUserRole() == UserRole.OPERATOR) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não possui permissão para essa operação");
            }
            List<ServicePendingDTO> tickets = service.getTicketsFinished();
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/hasPending")
    public ResponseEntity<?> hasPending(@RequestHeader("Authorization") String token) {
        try{
            Users user = userService.getUserFromToken(token);
            boolean pending = service.hasPending(user.getId());
            return ResponseEntity.ok(pending);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
