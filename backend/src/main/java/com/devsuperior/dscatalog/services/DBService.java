package com.devsuperior.dscatalog.services;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;

@Service
public class DBService {

	@Autowired
	private CategoryRepository repository;

	public void instanceBaseDate() throws ParseException {
		
		Category category = new Category(1L, "Eletronics");

		repository.save(category);

	}

}
