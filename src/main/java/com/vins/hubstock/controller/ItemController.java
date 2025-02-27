package com.vins.hubstock.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vins.hubstock.dto.ItemTransferDTO;
import com.vins.hubstock.dto.ItemsDTO;
import com.vins.hubstock.entity.ItemMovementHistory;
import com.vins.hubstock.entity.Items;
import com.vins.hubstock.entity.UserRole;
import com.vins.hubstock.entity.Users;
import com.vins.hubstock.service.ItemService;
import com.vins.hubstock.service.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/item")
@AllArgsConstructor
public class ItemController {

	private ItemService service;
	private UsersService userService;

	@GetMapping("/test")
	public ResponseEntity<?> testConnection() {
		try {
			return ResponseEntity.ok("Sucess");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/permission")
	public ResponseEntity<?> checkPermission(@RequestHeader("Authorization") String token) {
		try {
			Users user = userService.getUserFromToken(token);
			return ResponseEntity.ok(user.getUserRole());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Usuário não autorizado");
		}
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerItem(@RequestBody ItemsDTO item, @RequestHeader("Authorization") String token) {
		try {
			Users user = userService.getUserFromToken(token);
			if (user.getUserRole() == UserRole.USER) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Usuário não possui permissão para essa operação");
			}
			service.createItem(item);
			return ResponseEntity.ok(null);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Cadastro de item não concluído: " + e.getMessage());
		}
	}

	@PostMapping("/transfer")
	public ResponseEntity<?> transferItem(@RequestBody ItemTransferDTO transfer,
			@RequestHeader("Authorization") String token) {
		try {
			Users user = userService.getUserFromToken(token);
			if (user.getUserRole() == UserRole.USER) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Usuário não possui permissão para essa operação");
			}
			HttpStatus status = service.transfer(transfer, user);
			if (status == HttpStatus.OK) {
				return ResponseEntity.ok(null);
			} else {
				return ResponseEntity.status(status).build();
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/search")
	public ResponseEntity<?> searchItem(@RequestBody ItemsDTO search) {
		try {
			List<Items> items = service.search(search);
			return ResponseEntity.ok(items);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Falha ao pesquisar por um item");
		}
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<?> getItem(@PathVariable Long id) {
		try {
			Items item = service.getItem(id);
			if (item == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item não encontrado!");
			}

			List<ItemMovementHistory> history = item.getMovementHistory();
			Map<String, Object> response = new HashMap<>();
			response.put("item", item);
			response.put("history", history);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Falha ao recuperar o item!");
		}
	}

	@GetMapping("/clients")
	public ResponseEntity<?> clients() {
		try {
			return ResponseEntity.ok(service.getOwners());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Falha ao buscar pelos clientes");
		}
	}

	@GetMapping("/clients/{id}/units")
	public ResponseEntity<?> units(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.getUnits(id));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Falha ao buscar pelas unidades do cliente");
		}
	}

	@GetMapping("/quantity")
	public ResponseEntity<?> quantityItems(@RequestParam("owner") String owner, @RequestParam("unit") String unit) {
		try {
			if (unit.equals("Todos")) {
				return ResponseEntity.ok(service.getItemQuantityByClient(owner));
			} else {
				return ResponseEntity.ok(service.getItemQuantity(owner, unit));
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Falha ao obter a quantidade de itens na unidade");
		}
	}

	@PostMapping("/update")
	public ResponseEntity<?> updateInfo(@RequestBody ItemsDTO item, @RequestHeader("Authorization") String token) {
		try {
			Users user = userService.getUserFromToken(token);
			if (user.getUserRole() == UserRole.USER) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Usuário não possui permissão para essa operação");
			}
			service.updateItem(item, user);

			return ResponseEntity.ok(null);

		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body("Falha ao atualizar item! " + e.getMessage());
		}
	}

	@GetMapping("/moviment-report")
	public ResponseEntity<?> MovimentReport(
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate) {
		try {
			byte[] reportData = service.generateMovimentReport(startDate, endDate);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF); // Defina o tipo de conteúdo apropriado para o seu caso,
																// como PDF

			return ResponseEntity.ok().headers(headers).body(reportData);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}

	@GetMapping("/change-report")
	public ResponseEntity<?> ChangesReport(
			@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDate,
			@RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate) {
		try {
			byte[] reportData = service.generateChangeReport(startDate, endDate);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);

			return ResponseEntity.ok().headers(headers).body(reportData);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}

	@GetMapping("/quantity-report")
	public ResponseEntity<?> QuantityDetailedReport(@RequestParam("owner") String owner,
			@RequestParam("unit") String unit) {
		try {
			return ResponseEntity.ok(service.getQuantityDetailed(owner, unit));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Falha ao obter o quantitativo detalhado de itens");
		}
	}

}
