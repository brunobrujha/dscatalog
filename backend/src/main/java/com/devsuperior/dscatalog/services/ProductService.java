package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
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
	
}
