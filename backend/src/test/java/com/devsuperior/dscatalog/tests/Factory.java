package com.devsuperior.dscatalog.tests;

import java.time.Instant;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class Factory {

	public static Product createProduct() {
		Product product = new Product(1L,"Phone","Good Phone", 800.0, "Http://img.com/img.png", Instant.parse("2022-01-01T03:00:00Z"));
		product.getCategories().add(new Category(2L,"Eletronics"));
		return product;
	}
	
	public static Category createCategory() {
		Category category = new Category(1L,"Books");
		
		return category;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}
	
}
