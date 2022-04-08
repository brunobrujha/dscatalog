package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	private long existingId;
	private long noExistingId;
	private long foreingKeyExists;
	private PageImpl<Product> page;
	private Product product;
	private ProductDTO productDTO;
	private Category category;
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock 
	private CategoryRepository categoryRepository;
	
	@BeforeEach
	void setup() {
		existingId = 1L;
		noExistingId = 1000L;
		foreingKeyExists = 4L;
		product = Factory.createProduct();
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(product));
		
		//Delete - Quando o retorno e void
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).deleteById(noExistingId);
		Mockito.doThrow(DataIntegrityException.class).when(repository).deleteById(foreingKeyExists);
		
		
		//Quando o retorno nao e void a syntaxe e com when primeiro
		//FindAll
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		//FindById
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(noExistingId)).thenThrow(EntityNotFoundException.class);
		
		
		//Save
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(repository.getOne(noExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExist() {
		ProductDTO dto = service.findById(existingId);
		
		Assertions.assertNotNull(dto);
		Mockito.verify(repository).findById(existingId);
		
	}
	
	@Test
	public void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(EntityNotFoundException.class, () ->{
			service.findById(noExistingId);
		});
		Mockito.verify(repository).findById(noExistingId);
		
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExist() {
				
		ProductDTO result = service.update(existingId, productDTO);
		
		Assertions.assertNotNull(result);
		
	}
	
	@Test
	public void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
		
		Assertions.assertThrows(EntityNotFoundException.class, () ->{
			service.update(noExistingId, productDTO);
		});		
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable  = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository , Mockito.times(1)).findAll(pageable);
		
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() ->{
			service.delete(existingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);	
	}
	
	@Test
	public void deleteShouldThrowEntityNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(EntityNotFoundException.class, () ->{
			service.delete(noExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(noExistingId);	
	}

	@Test
	public void deleteShouldThrowDataIntegrityExceptionWhenFKDependenceExists() {
		
		Assertions.assertThrows(DataIntegrityException.class, () ->{
			service.delete(foreingKeyExists);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(foreingKeyExists);	
	}
	
	
	
}
