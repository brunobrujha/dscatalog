package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setup() throws Exception{
		
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 2L;
		
		productDTO = Factory.createProductDTO();
		
		page = new PageImpl<>(List.of(productDTO));
		
		when(service.findAllPaged(any())).thenReturn(page);
		
		when(service.insert(any())).thenReturn(productDTO);
				
		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		when(service.update(eq(existingId) , any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(EntityNotFoundException.class);
		
		doNothing().when(service).delete(existingId);
		doThrow(EntityNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DataIntegrityException.class).when(service).delete(dependentId);
	}
	
	@Test
	public void insertShouldReturnProductDTO() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		mockMvc.perform(post("/producties").content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").exists());
	}
	
	@Test
	public void findAllShoudReturnPage() throws Exception {
		mockMvc.perform(get("/producties")).andExpect(status().isOk());

	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
		
	//	ResultActions result = mockMvc.perform(get("/producties/{id}", existingId).accept(MediaType.APPLICATION_JSON));
		
	//	result.andExpect(status().isOk());
	//	result.andExpect(jsonPath("$.id").exists());
		
		mockMvc.perform(get("/producties/{id}", existingId).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(jsonPath("$.id").exists());
		
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		mockMvc.perform(get("/producties/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
		
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		mockMvc.perform(put("/producties/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		mockMvc.perform(put("/producties/{id}", nonExistingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		mockMvc.perform(delete("/producties/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		mockMvc.perform(delete("/producties/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());	
	}
	
}
