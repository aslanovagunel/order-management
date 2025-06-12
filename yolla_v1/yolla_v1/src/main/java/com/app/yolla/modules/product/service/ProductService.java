package com.app.yolla.modules.product.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.yolla.modules.product.dto.ProductAddRequest;
import com.app.yolla.modules.product.dto.ProductDTO;
import com.app.yolla.modules.product.entity.Product;
import com.app.yolla.modules.product.repository.ProductRepository;
import com.app.yolla.modules.user.dto.UserDTO;
import com.app.yolla.modules.user.service.UserService;
import com.app.yolla.shared.exception.MyException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private UserService userService;

	public ProductDTO createdProduct(@Valid ProductAddRequest req) {
		String phone = (String) userService.findPhone();
		UserDTO en = userService.findByPhoneNumber(phone);

		Product product = new Product();
		mapper.map(req, product);
		product.setCreatedAt(LocalDateTime.now());
		product.setUserId(en.getId());
		product.setActive(true);
		repository.save(product);

		return convertToDTO(product);

	}

	private ProductDTO convertToDTO(Product product) {
		return new ProductDTO(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
				product.getStockQuantity(),
				product.getActive(), product.getCreatedAt(), product.getUpdatedAt());
	}

	public Product findProduct(Long id) {
		Optional<Product> op = repository.findById(id);
		if (!op.isPresent()) {
			throw new MyException("bu id'li mehsul yoxdur");
		}
		Product p = op.get();
		return p;

	}
}
