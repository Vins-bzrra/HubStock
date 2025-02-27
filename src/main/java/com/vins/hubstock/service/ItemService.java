package com.vins.hubstock.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


import com.vins.hubstock.entity.*;
import com.vins.hubstock.repository.*;
import com.vins.hubstock.dto.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
@AllArgsConstructor
public class ItemService {

	private ItemRepository repository;
	private ItemChangeRepository changeRepository;
	private ItemMovementHistoryRepository historyRepository;
	private ClientsRepository clientsRepository;
	private UnitsRepository unitsRepository;

	public void createItem(ItemsDTO itemDto) {
		try {
			Items item = new Items();
			item.setBrand(itemDto.getBrand());
			item.setAcquisitionDate(itemDto.getAcquisitionDate());
			item.setCategory(itemDto.getCategory());
			item.setCurrentOwner(itemDto.getCurrentOwner());
			item.setDescription(itemDto.getDescription());
			item.setModel(itemDto.getModel());

			if (isPatrimonyInUse(itemDto.getPatrimony())) {
				throw new RuntimeException("O número de patrimônio já está em uso por outro item.");
			}

			if (isSerialUnique(itemDto.getSerialNumber())) {
				throw new RuntimeException("O Serial informado já está registrado em outro item.");
			}

			item.setPatrimony(itemDto.getPatrimony());
			item.setSerialNumber(itemDto.getSerialNumber());
			item.setStatus(itemDto.getStatus());
			item.setSupplier(itemDto.getSupplier());
			item.setUnitLocation(itemDto.getUnitLocation());

			repository.save(item);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Falha ao registrar o item", e);
		}
	}

	public HttpStatus transfer(ItemTransferDTO transfer, Users user) {
		try {
			String patrimony = transfer.getPatrimony();
			String newOwner = transfer.getNewOwner();
			String newUnitLocation = transfer.getNewUnitLocation();

			Optional<Items> optionalItem = repository.findByPatrimony(patrimony);
			if (optionalItem.isEmpty()) {
				throw new RuntimeException("Item não localizado!");
			}

			Items item = optionalItem.get();
			String previousOwner = item.getCurrentOwner();
			String previousUnitLocation = item.getUnitLocation();

			boolean isValidNewUnitLocation = newUnitLocation != null && !newUnitLocation.isEmpty()
					&& newUnitLocation.length() <= 150;

			boolean isValidNewOwner = newOwner != null && !newOwner.isEmpty() && newOwner.length() <= 100;

			if (isValidNewOwner && !isValidNewUnitLocation) {
				item.setCurrentOwner(newOwner);
			} else if (!isValidNewOwner && isValidNewUnitLocation) {
				item.setUnitLocation(newUnitLocation);
			} else if (isValidNewOwner && isValidNewUnitLocation) {
				item.setCurrentOwner(newOwner);
				item.setUnitLocation(newUnitLocation);
			} else {
				throw new RuntimeException("Nenhuma opção de transferência preenchida");
			}

			ItemMovementHistory movementHistory = new ItemMovementHistory();
			movementHistory.setItem(item);
			movementHistory.setPreviousOwner(previousOwner);
			movementHistory.setPreviousUnitLocation(previousUnitLocation);
			movementHistory.setNewOwner(item.getCurrentOwner());
			movementHistory.setNewUnitLocation(item.getUnitLocation());
			movementHistory.setMovementType("Transferência");
			movementHistory.setNameUser(user.getName() + " " + user.getLastName());
			movementHistory.setRegistrationUser(user.getRegistrationNumber());
			movementHistory.setMovementDateTime(LocalDateTime.now());

			historyRepository.save(movementHistory);

			repository.save(item);

			return HttpStatus.OK;
		} catch (Exception e) {
			return HttpStatus.BAD_GATEWAY;
		}
	}

	public List<Items> search(ItemsDTO search) {
		try {
			Specification<Items> specification = (root, query, criteriaBuilder) -> {
				List<Predicate> predicates = new ArrayList<>();

				if (search.getBrand() != null) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("brand")),
							"%" + search.getBrand().toUpperCase() + "%"));
				}

				if (search.getModel() != null) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("model")),
							"%" + search.getModel().toUpperCase() + "%"));
				}

				if (search.getPatrimony() != null) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("patrimony")),
							"%" + search.getPatrimony().toUpperCase() + "%"));
				}

				if (search.getAcquisitionDate() != null) {
					predicates.add(criteriaBuilder.equal(root.get("acquisitionDate"), search.getAcquisitionDate()));
				}

				if (search.getSupplier() != null) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("supplier")),
							"%" + search.getSupplier().toUpperCase() + "%"));
				}

				if (search.getUnitLocation() != null) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("unitLocation")),
							"%" + search.getUnitLocation().toUpperCase() + "%"));
				}

				if (search.getStatus() != null) {
					predicates.add(criteriaBuilder.equal(root.get("status"), search.getStatus()));
				}

				if (search.getCurrentOwner() != null) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("currentOwner")),
							"%" + search.getCurrentOwner().toUpperCase() + "%"));
				}

				if (search.getCategory() != null) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("category")),
							"%" + search.getCategory().toUpperCase() + "%"));
				}

				if (search.getSerialNumber() != null) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("serialNumber")),
							"%" + search.getSerialNumber().toUpperCase() + "%"));
				}

				if (search.getDescription() != null) {
					predicates.add(criteriaBuilder.like(criteriaBuilder.upper(root.get("description")),
							"%" + search.getDescription().toUpperCase() + "%"));
				}

				// Combina todas as condições com o operador "AND" e retorna o resultado
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			};

			return repository.findAll(specification);

		} catch (Exception e) {
			throw new RuntimeException("Falha ao pesquisar por um item!");
		}

	}

	public Items getItem(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Transactional
	public void updateItem(ItemsDTO itemDto, Users user) {
		try {
			Optional<Items> optionalItem = repository.findById(itemDto.getId());
			if (optionalItem.isPresent()) {
				Items item = optionalItem.get();
				String patrimony = item.getPatrimony();
				if (itemDto.getPatrimony() != null && !itemDto.getPatrimony().isBlank()
						&& !itemDto.getPatrimony().equals(patrimony)) {
					if (isPatrimonyInUse(itemDto.getPatrimony())) {
						throw new RuntimeException("O número de patrimônio já está em uso por outro item.");
					}

				}

				if (itemDto.getSerialNumber() != null && !itemDto.getSerialNumber().isBlank()
						&& !itemDto.getSerialNumber().equals(item.getSerialNumber())) {
					if (isSerialUnique(itemDto.getSerialNumber())) {
						throw new RuntimeException("O Serial informado já está registrado em outro item.");
					}

				}

				if (itemDto.getBrand() != null && !itemDto.getBrand().isEmpty()
						&& !itemDto.getBrand().equals(item.getBrand())) {
					registerItemChange("Marca", item.getBrand(), itemDto.getBrand(), item.getPatrimony(), user);
					item.setBrand(itemDto.getBrand());
				}

				if (itemDto.getModel() != null && !itemDto.getModel().isEmpty()
						&& !itemDto.getModel().equals(item.getModel())) {
					registerItemChange("Modelo", item.getModel(), itemDto.getModel(), item.getPatrimony(), user);
					item.setModel(itemDto.getModel());
				}

				if (itemDto.getAcquisitionDate() != null
						&& !itemDto.getAcquisitionDate().equals(item.getAcquisitionDate())) {
					registerItemChange("Data de Aquisição", item.getAcquisitionDate().toString(),
							itemDto.getAcquisitionDate().toString(), item.getPatrimony(), user);
					item.setAcquisitionDate(itemDto.getAcquisitionDate());
				}

				if (itemDto.getSupplier() != null && !itemDto.getSupplier().isEmpty()
						&& !itemDto.getSupplier().equals(item.getSupplier())) {
					registerItemChange("Fornecedor", item.getSupplier(), itemDto.getSupplier(), item.getPatrimony(),
							user);
					item.setSupplier(itemDto.getSupplier());
				}

				if (itemDto.getUnitLocation() != null && !itemDto.getUnitLocation().isEmpty()
						&& !itemDto.getUnitLocation().equals(item.getUnitLocation())) {
					registerItemChange("Unidade", item.getUnitLocation(), itemDto.getUnitLocation(),
							item.getPatrimony(), user);
					item.setUnitLocation(itemDto.getUnitLocation());
				}

				if (itemDto.getStatus() != null && !itemDto.getStatus().isEmpty()
						&& !itemDto.getStatus().equals(item.getStatus())) {
					registerItemChange("Status", item.getStatus(), itemDto.getStatus(), item.getPatrimony(), user);
					item.setStatus(itemDto.getStatus());
				}

				if (itemDto.getCurrentOwner() != null && !itemDto.getCurrentOwner().isEmpty()
						&& !itemDto.getCurrentOwner().equals(item.getCurrentOwner())) {
					registerItemChange("Proprietário", item.getCurrentOwner(), itemDto.getCurrentOwner(),
							item.getPatrimony(), user);
					item.setCurrentOwner(itemDto.getCurrentOwner());
				}

				if (itemDto.getCategory() != null && !itemDto.getCategory().isEmpty()
						&& !itemDto.getCategory().equals(item.getCategory())) {
					registerItemChange("Categoria", item.getCategory(), itemDto.getCategory(), item.getPatrimony(),
							user);
					item.setCategory(itemDto.getCategory());
				}

				if (itemDto.getSerialNumber() != null && !itemDto.getSerialNumber().isEmpty()
						&& !itemDto.getSerialNumber().equals(item.getSerialNumber())) {
					registerItemChange("Serial Number", item.getSerialNumber(), itemDto.getSerialNumber(),
							item.getPatrimony(), user);
					item.setSerialNumber(itemDto.getSerialNumber());
				}

				if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()
						&& !itemDto.getDescription().equals(item.getDescription())) {
					registerItemChange("Descrição", item.getDescription(), itemDto.getDescription(),
							item.getPatrimony(), user);
					item.setDescription(itemDto.getDescription());
				}

				if (itemDto.getPatrimony() != null && !itemDto.getPatrimony().isEmpty()
						&& !itemDto.getPatrimony().equals(item.getPatrimony())) {
					registerItemChange("Patrimônio", item.getPatrimony(), itemDto.getPatrimony(), item.getPatrimony(),
							user);
					item.setPatrimony(itemDto.getPatrimony());
				}
				repository.save(item);
			} else {
				throw new RuntimeException("Item não encontrado");
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Falha ao atualizar o item", e);
		}

	}

	public boolean isPatrimonyInUse(String numeroPatrimonio) {
        return repository.findByPatrimony(numeroPatrimonio).isPresent();
	}

	public boolean isSerialUnique(String serial) {
        return repository.findBySerialNumber(serial).isPresent();
	}

	@Transactional
	public void registerItemChange(String attributeName, String oldValue, String newValue, String patrimony,
			Users user) {
		try {
			if (attributeName == null || attributeName.isBlank() || oldValue == null || oldValue.isBlank()
					|| newValue == null || newValue.isBlank() || patrimony == null || patrimony.isBlank()
					|| user == null) {
				throw new IllegalArgumentException("Argumentos inválidos para registrar a mudança do item.");
			}

			ItemChange changeLog = new ItemChange();
			changeLog.setPatrimonyItem(patrimony);
			changeLog.setAttributeName(attributeName);
			changeLog.setOldValue(oldValue);
			changeLog.setNewValue(newValue);
			changeLog.setChangeDateTime(LocalDateTime.now());
			changeLog.setUserRegistration(user.getRegistrationNumber());
			changeLog.setUserName(user.getName() + " " + user.getLastName());

			changeRepository.save(changeLog);

		} catch (DataAccessException e) {
			throw new RuntimeException("Falha ao registrar mudança do item: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Falha ao registrar mudança do item.");
		}
	}

	public byte[] generateMovimentReport(LocalDateTime startDate, LocalDateTime endDate) {
		try {
			List<ItemMovementHistory> history = historyRepository.findByMovementDateTimeBetween(startDate, endDate);
			if (history.isEmpty()) {
				throw new RuntimeException("Não há registros de movimento dentro do período especificado.");
			}

			JasperReport jasperReport = JasperCompileManager
					.compileReport(getClass().getResourceAsStream("/modelMoviment.jrxml"));

			DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedStartDate = startDate.format(targetFormatter);
			String formattedEndDate = endDate.format(targetFormatter);

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("startDate", formattedStartDate);
			parameters.put("endDate", formattedEndDate);

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(history);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			JRPdfExporter pdfExporter = new JRPdfExporter();
			pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));

			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfStream));
			pdfExporter.exportReport();

			return pdfStream.toByteArray();
		} catch (JRException e) {
			throw new RuntimeException("Erro ao gerar o relatório. Por favor, tente novamente.");
		}
	}

	public byte[] generateChangeReport(LocalDateTime startDate, LocalDateTime endDate) {
		try {
			List<ItemChange> history = changeRepository.findByChangeDateTimeBetween(startDate, endDate);
			if (history.isEmpty()) {
				throw new RuntimeException("Não há registros de movimento dentro do período especificado.");
			}

			JasperReport jasperReport = JasperCompileManager
					.compileReport(getClass().getResourceAsStream("/modelChange.jrxml"));

			DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedStartDate = startDate.format(targetFormatter);
			String formattedEndDate = endDate.format(targetFormatter);

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("startDate", formattedStartDate);
			parameters.put("endDate", formattedEndDate);

			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(history);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			JRPdfExporter pdfExporter = new JRPdfExporter();
			pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));

			ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
			pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfStream));
			pdfExporter.exportReport();

			// Retorna o PDF como um array de bytes
			return pdfStream.toByteArray();
		} catch (JRException e) {
			throw new RuntimeException("Erro ao gerar o relatório. Por favor, tente novamente.");
		}
	}

	public List<Clients> getOwners() {
		List<Clients> clients = clientsRepository.findAll();
		return clients;
	}

	public List<Units> getUnits(Long idOwner) {
		List<Units> units = unitsRepository.findByClientId(idOwner);
		return units;
	}
	
	public List<ItemsCount> getItemQuantity(String owner, String unit) {
	    List<Items> items = repository.findByCurrentOwnerAndUnitLocation(owner, unit);

	    // Normaliza os campos removendo espaços extras e convertendo para letras minúsculas
	    Map<QuantityMap, Long> itemCountMap = items.stream()
	            .collect(Collectors.groupingBy(item -> new QuantityMap(
	                    item.getModel().trim(),
	                    item.getBrand().trim(),
	                    item.getCategory().trim()),
	                    Collectors.counting()));

	    List<ItemsCount> itemsCountList = itemCountMap.entrySet().stream()
	            .map(entry -> {
	                QuantityMap key = entry.getKey();
	                return new ItemsCount(
	                        key.getModel(), 
	                        key.getBrand(), 
	                        key.getCategory(), 
	                        entry.getValue());
	            })
	            .collect(Collectors.toList());

	    return itemsCountList;
	}
	
	public List<QuantityDetailed> getQuantityDetailed(String owner, String unit) {
		List<Items> items = repository.findByCurrentOwnerAndUnitLocation(owner, unit);
	
	    return items.stream()
	            .map(item -> new QuantityDetailed(
	                    item.getBrand(),
	                    item.getModel(),
	                    item.getPatrimony(),
	                    item.getSerialNumber()
	                ))
	                .collect(Collectors.toList());
	}

	public List<UnitItemCount> getItemQuantityByClient(String owner) {
	    List<Items> items = repository.findByCurrentOwner(owner);

	    // Normaliza os campos removendo espaços extras e convertendo para letras minúsculas,
	    // depois agrupa os itens por unidade e pelos campos desejados
	    Map<String, Map<QuantityMap, Long>> groupedByUnit = items.stream()
	            .collect(Collectors.groupingBy(Items::getUnitLocation, 
	                    Collectors.groupingBy(item -> new QuantityMap(
	                            item.getModel().trim(),
	                            item.getBrand().trim(),
	                            item.getCategory().trim()), 
	                            Collectors.counting())));

	    List<UnitItemCount> unitItemCountList = groupedByUnit.entrySet().stream()
	            .flatMap(unitEntry -> unitEntry.getValue().entrySet().stream()
	                    .map(entry -> {
	                        QuantityMap key = entry.getKey();
	                        return new UnitItemCount(
	                                unitEntry.getKey(),
	                                key.getModel(), 
	                                key.getBrand(), 
	                                key.getCategory(), 
	                                entry.getValue());
	                    }))
	            .collect(Collectors.toList());

	    return unitItemCountList;
	}

}
