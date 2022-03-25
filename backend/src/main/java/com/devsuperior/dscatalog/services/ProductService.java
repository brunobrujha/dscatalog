package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public List<ProductDTO> findAll(){
		
		List<Product> list  = repository.findAll();
		
		return list.stream().map(prod -> new ProductDTO(prod)).collect(Collectors.toList());
		
	}
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
		
		Page<Product> list = repository.findAll(pageRequest);
		
		return list.map(prod -> new ProductDTO(prod));
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id){
		
		Optional<Product> obj = repository.findById(id);
		
		Product entity = obj.orElseThrow( () -> new EntityNotFoundException("Entity not found"));
		
		return new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {

		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}
	
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product entity = repository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);
		} catch (javax.persistence.EntityNotFoundException e) {
			throw new EntityNotFoundException("Id not found: " + id);
		}
	}
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new EntityNotFoundException("Id not found: " + id);
		}catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Integrity vaolation");
		}
		
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setPrice(dto.getPrice());
		entity.setDescription(dto.getDescription());
		entity.setImgUrl(dto.getImgUrl());
		entity.setDate(dto.getDate());
		
		entity.getCategories().clear();
		
		for (CategoryDTO catDTO : dto.getCategories()) {
			Category category  = categoryRepository.getOne(catDTO.getId());
			entity.getCategories().add(category);
		}
	}
	
}
