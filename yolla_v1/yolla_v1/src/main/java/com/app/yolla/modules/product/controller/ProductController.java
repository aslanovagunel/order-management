package com.app.yolla.modules.product.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.modules.product.dto.ProductAddRequest;
import com.app.yolla.modules.product.dto.ProductDTO;
import com.app.yolla.modules.product.dto.ProductUpdateRequest;
import com.app.yolla.modules.product.service.ProductService;
import com.app.yolla.shared.dto.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Product", description = "Məhsullar üzrə idarəetmə və axtarış əməliyyatları")
@RestController
@RequestMapping(path = "/products")
@CrossOrigin(origins = "*") // CORS icazəsi
public class ProductController {

	@Autowired
	private ProductService service;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<ProductDTO>> add(@Valid @RequestBody ProductAddRequest req,
			BindingResult result) {
		 try {
			 ProductDTO createdProduct = service.createdProduct(req);

	            ApiResponse<ProductDTO> response = new ApiResponse<>(
	                    true,
	                    "Mehsul uğurla yaradıldı",
	                    createdProduct
	            );

	            return ResponseEntity.status(HttpStatus.CREATED).body(response);

	        } catch (Exception e) {

				ApiResponse<ProductDTO> response = new ApiResponse<>(
	                    false,
						"Mehsulu yaradılarkən xəta baş verdi: " + e.getMessage(),
	                    null
	            );

	            return ResponseEntity.badRequest().body(response);
			}
		}

		@PutMapping
		@PreAuthorize("hasRole('ADMIN')")
		public ResponseEntity<ApiResponse<ProductDTO>> update(@Valid @RequestBody ProductUpdateRequest req,
				BindingResult result) {

			try {
				ProductDTO updateProduct = service.updateProduct(req);

				ApiResponse<ProductDTO> response = new ApiResponse<>(true, "Məhsul uğurla dəyişdirildi", updateProduct);

				return ResponseEntity.ok(response);

			} catch (Exception e) {
				ApiResponse<ProductDTO> response = new ApiResponse<>(false,
						"Məhsul dəyişdirilərkən xəta baş verdi: " + e.getMessage(), null);

				return ResponseEntity.badRequest().body(response);
			}
		}


		@DeleteMapping(path = "/{id}")
		@PreAuthorize("hasRole('ADMIN')")
		public ResponseEntity<?> deleteById(@PathVariable UUID id) {

			try {
				service.deleteById(id);

				ApiResponse<String> response = new ApiResponse<>(true, "Məhsul uğurla silindi", null);

				return ResponseEntity.ok(response);

			} catch (Exception e) {
				ApiResponse<String> response = new ApiResponse<>(false,
						"Məhsul dəyişdirilərkən xəta baş verdi: " + e.getMessage(), null);

				return ResponseEntity.badRequest().body(response);
			}
		}
}
