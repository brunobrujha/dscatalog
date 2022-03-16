package com.devsuperior.dscatalog.entities;

import java.io.Serializable;
import java.util.Objects;

public class Category implements Serializable {


	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String mane;
	
	public Category() {}

	public Category(Long id, String mane) {
		this.id = id;
		this.mane = mane;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMane() {
		return mane;
	}

	public void setMane(String mane) {
		this.mane = mane;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		return Objects.equals(id, other.id);
	}
	
	
	
	
}
