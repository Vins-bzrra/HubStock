package com.vins.hubstock.service;

import com.vins.hubstock.dto.ServicePendingDTO;
import com.vins.hubstock.dto.ServiceRequestDTO;
import com.vins.hubstock.entity.ServiceRequest;
import com.vins.hubstock.entity.UserRole;
import com.vins.hubstock.entity.Users;
import com.vins.hubstock.repository.ServiceRequestRepository;
import com.vins.hubstock.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRequestService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    public void createServiceTicket(ServiceRequestDTO service, Users user) {
        try {
            ServiceRequest ticket = new ServiceRequest();
            ticket.setAuthorName(user.getName());
            ticket.setAuthorRegistration(user.getRegistrationNumber());
            Users requested = userRepository.findByRegistrationNumber(service.getRequestedRegistration()).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            ticket.setUserRequested(requested);
            ticket.setDateServiceExecution(service.getDateServiceExecution());
            ticket.setItem(service.getItem());
            ticket.setQuantity(service.getQuantity());
            ticket.setDateCreateService(LocalDateTime.now());
            ticket.setDescription(service.getDescription());
            ticket.setIscompleted(false);
            ticket.setTitle(service.getTitle());
            serviceRequestRepository.save(ticket);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar Ticket. Por favor, tente novamente.");
        }

    }

    public void finishServiceTicket(Users user, Long id) {
        try {
            ServiceRequest service = serviceRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            service.setFinishBy(user.getRegistrationNumber());
            service.setIscompleted(true);
            serviceRequestRepository.save(service);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao concluir Ticket. Por favor, tente novamente.");
        }
    }

    public boolean hasPending(Long userId) {
        return !serviceRequestRepository.findPendingByUserId(userId).isEmpty();
    }

    public List<ServicePendingDTO> getServiceTickets(Users user) {
        try {
            List<ServiceRequest> pendingRequests = new ArrayList<>();
            if (user.getUserRole().equals(UserRole.ADMIN)) {
                pendingRequests = serviceRequestRepository.findAllPending();
            }else {
                pendingRequests = serviceRequestRepository.findPendingByUserId(user.getId());
            }
            return pendingRequests.stream()
                    .map(this::convertServicePending)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao recuperar tickets pendentes. Por favor, tente novamente.");
        }
    }

    public List<ServicePendingDTO> getTicketsFinished() {
        try {
            List<ServiceRequest> pendingRequests = serviceRequestRepository.findAllFinish();

            return pendingRequests.stream()
                    .map(this::convertServicePending)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao recuperar tickets pendentes. Por favor, tente novamente.");
        }
    }

    private ServicePendingDTO convertServicePending(ServiceRequest request) {
        ServicePendingDTO dto = new ServicePendingDTO();
        dto.setServiceId(request.getId());
        dto.setTitle(request.getTitle());
        dto.setStatus(request.getIscompleted());
        dto.setItem(request.getItem());
        dto.setQuantity(request.getQuantity());
        dto.setDescription(request.getDescription());
        dto.setAuthorName(request.getAuthorName());
        dto.setDateServiceExecution(request.getDateServiceExecution());
        dto.setNameUserRequested(request.getUserRequested().getName() + " " + request.getUserRequested().getLastName());
        return dto;
    }

}
