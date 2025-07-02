package com.app.yolla.modules.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.yolla.modules.order.dto.OrderCreateRequest;
import com.app.yolla.modules.order.dto.OrderDTO;
import com.app.yolla.modules.order.dto.OrderItemDTO;
import com.app.yolla.modules.order.dto.OrderItemRequest;
import com.app.yolla.modules.order.dto.OrderItemUpdateRequest;
import com.app.yolla.modules.order.dto.OrderResponse;
import com.app.yolla.modules.order.dto.OrderUpdateRequest;
import com.app.yolla.modules.order.entity.Order;
import com.app.yolla.modules.order.entity.OrderItem;
import com.app.yolla.modules.order.entity.OrderStatus;
import com.app.yolla.modules.order.repository.OrderRepository;
import com.app.yolla.modules.product.entity.Product;
import com.app.yolla.modules.product.service.ProductService;
import com.app.yolla.modules.user.dto.UserDTO;
import com.app.yolla.modules.user.entity.UserRole;
import com.app.yolla.modules.user.service.UserService;
import com.app.yolla.shared.exception.MyException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	public OrderDTO createdOrder(OrderCreateRequest request) {
		String phone = (String) userService.findPhone();
		UserDTO byPhoneNumber = userService.findByPhoneNumber(phone);

		Order order = new Order();
		mapper.map(request, order);
		order.setCreatedAt(LocalDateTime.now());
		order.setUserId(byPhoneNumber.getId());
		

		List<OrderItemRequest> items2 = request.getItems();
		List<OrderItem> items = new ArrayList<>();

		BigDecimal totalAmount = BigDecimal.ZERO;
		for (OrderItemRequest req : items2) {
			Product product = productService.findProduct(req.getProductId());

		    OrderItem orderItem = new OrderItem();
		    orderItem.setOrder(order);
		    orderItem.setProduct(product);
			if (!(req.getQuantity() <= product.getStockQuantity() && req.getQuantity() > 0)) {
				throw new MyException("Stokda kifayət qədər məhsul yoxdur");
			}
			orderItem.setQuantity(req.getQuantity());
			BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
			totalAmount = totalAmount.add(itemTotal);
		    items.add(orderItem);
			product.setStockQuantity(product.getStockQuantity() - req.getQuantity());
			productService.savePro(product);
		}
		order.setTotalAmount(totalAmount);
		order.setItems(items);

		repository.save(order);

		return convertToDTO(order);
	}

	private OrderDTO convertToDTO(Order order) {
		List<OrderItemDTO> itemDTOs = new ArrayList<OrderItemDTO>();
		List<OrderItem> items = order.getItems();

		if (items == null || items.isEmpty()) {
			throw new MyException("Sifariş detalları yoxdur");
		}
		for (OrderItem o : items) {
			OrderItemDTO dto=new OrderItemDTO();
			dto.setProductId(o.getProduct().getId());
			dto.setProductName(o.getProduct().getName());
			dto.setQuantity(o.getQuantity());
			dto.setUnitPrice(o.getProduct().getPrice());
			itemDTOs.add(dto);
		}
		
		UserDTO op = userService.findById(order.getUserId());
		OrderDTO dto = new OrderDTO();
		dto.setId(order.getId());
		dto.setUserId(order.getUserId());
		dto.setUserFullName(op.getFullName());
		dto.setUserPhone(op.getPhoneNumber());
		dto.setStatus(order.getStatus());
		dto.setTotalAmount(order.getTotalAmount());
		dto.setCreatedAt(order.getCreatedAt());
		dto.setNotes(order.getNotes());
		dto.setDeliveryAddress(order.getDeliveryAddress());
		dto.setItems(itemDTOs);
		dto.setDeliveryTime(order.getDeliveryTime());

		return dto;
	}

	public OrderResponse getAll(Integer begin, Integer length) {
		String phone = (String) userService.findPhone();
		UserDTO en = userService.findByPhoneNumber(phone);

		OrderResponse response = new OrderResponse();
		List<Order> all = repository.getAll(en.getId(), begin, length);
		List<OrderDTO> list = new ArrayList<OrderDTO>();

		for (Order order : all) {
			OrderDTO convertToDTO = convertToDTO(order);
			list.add(convertToDTO);
		}
		response.setList(list);
		return response;
	}

	public List<Order> findOrder(UUID id) {
		List<Order> p=repository.findOrder(id);
		return p;
	}

	public Order findByOrder(UUID id) {
		Optional<Order> op = repository.findById(id);
		if (!op.isPresent()) {
			throw new MyException("Bu id'li sifariş yoxdur");
		}
		Order p = op.get();
		return p;

	}

	public void deleteById(UUID id) {
		String phone = (String) userService.findPhone();
		UserDTO currentUser = userService.findByPhoneNumber(phone);

		Order order = findByOrder(id);
		if (!order.getUserId().equals(currentUser.getId())) {
			throw new MyException("Başqasının sifarişi silinə bilməz");
		}

		repository.deleteById(id);

	}

	public OrderDTO updateOrder(UUID id, OrderUpdateRequest req) {
		String phone = (String) userService.findPhone();
		UserDTO currentUser = userService.findByPhoneNumber(phone);

		Order order = findByOrder(id);
		if (!order.getUserId().equals(currentUser.getId())) {
			throw new MyException("Başqasının sifarişi redaktə edilə bilməz");
		}

		if (req.getStatus() != null) {
			order.setStatus(req.getStatus());
			if (req.getStatus() == OrderStatus.DELIVERED && order.getDeliveryTime() == null) {
				order.setDeliveryTime(LocalDateTime.now());
			}
		}
		if (req.getNotes() != null && !req.getNotes().trim().isEmpty()) {
			order.setNotes(req.getNotes());
		}
		if (req.getDeliveryAddress() != null && !req.getDeliveryAddress().trim().isEmpty()) {
			order.setDeliveryAddress(req.getDeliveryAddress());
		}
		if (req.getItems() != null) {
			List<OrderItemUpdateRequest> items = req.getItems();
			List<OrderItem> existingItems = order.getItems();

			for (OrderItemUpdateRequest o : items) {
				Optional<OrderItem> existingItemOpt = existingItems.stream()
						.filter(i -> i.getProduct().getId().equals(o.getProductId())).findFirst();

				Product product = productService.findProduct(o.getProductId());

				if (existingItemOpt.isPresent()) {
					OrderItem existingItem = existingItemOpt.get();

					int oldQuantity = existingItem.getQuantity(); // köhnə miqdar
					int newQuantity = o.getQuantity(); // yenilənmiş miqdar

					int difference = newQuantity - oldQuantity;
					int currentStock = product.getStockQuantity();

					if (newQuantity <= 0) {
						throw new MyException("Miqdar sıfırdan böyük olmalıdır");
					}

					if (difference > 0) {
						// stokda kifayət qədər məhsul yoxdursa, xətaya səbəb olur
						if (currentStock < difference) {
							throw new MyException("Stokda kifayət qədər məhsul yoxdur");
						}
						product.setStockQuantity(currentStock - difference);
					} else if (difference < 0) {
						// artan stok miqdarı
						product.setStockQuantity(currentStock + (-difference));
					}

					// Miqdarı yenilə (stok yoxlamasından sonra)
					existingItem.setQuantity(newQuantity);

					// Qiyməti yenilə (məsələn, ədədi * vahid qiymət)
					existingItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));

					productService.savePro(product);
				} else {
					// Yeni OrderItem əlavə etmək üçün buraya kod əlavə edə bilərsən
				}
			}

			// Order-un ümumi məbləğini yenidən hesabla
			BigDecimal totalAmount = BigDecimal.ZERO;
			for (OrderItem item : existingItems) {
				totalAmount = totalAmount.add(item.getPrice());
			}
			order.setTotalAmount(totalAmount);
		}

		return convertToDTO(order);
	}

	public OrderDTO confirmOrder(UUID orderId, String currentUserPhone) {
		Order order = findByOrder(orderId);

		UserDTO currentUser = userService.findByPhoneNumber(currentUserPhone);

		boolean isAdmin = currentUser.getRole().equals(UserRole.ADMIN);
		boolean isOwner = order.getUserId().equals(currentUser.getId());

		if (!isAdmin && !isOwner) {
			throw new MyException("Bu əməliyyatı icra etmək üçün icazəniz yoxdur");
		}

		if (order.getStatus() != OrderStatus.PENDING) {
			throw new MyException("Yalnız PENDING statusundakı sifariş təsdiqlənə bilər");
		}

		order.setStatus(OrderStatus.CONFIRMED);
		repository.save(order);

		return convertToDTO(order);
	}

	public OrderDTO shipOrder(UUID orderId, String currentUserPhone) {
		Order order = findByOrder(orderId);

		UserDTO currentUser = userService.findByPhoneNumber(currentUserPhone);

		boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
		boolean isPreparer = currentUser.getRole() == UserRole.PREPARER;

		if (!isAdmin && !isPreparer) {
			throw new MyException("Bu əməliyyatı icra etmək üçün icazəniz yoxdur");
		}

		if (order.getStatus() != OrderStatus.CONFIRMED) {
			throw new MyException("Yalnız CONFIRMED statusundakı sifariş göndərilə bilər");
		}

		order.setStatus(OrderStatus.SHIPPED);

		repository.save(order);

		return convertToDTO(order);
	}

	public OrderDTO deliverOrder(UUID id, String currentUserPhone) {
		Order order = findByOrder(id);

		UserDTO currentUser = userService.findByPhoneNumber(currentUserPhone);

		boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
		boolean isPreparer = currentUser.getRole() == UserRole.PREPARER;

		if (!isAdmin && !isPreparer) {
			throw new MyException("Bu əməliyyatı icra etmək üçün icazəniz yoxdur");
		}

		if (order.getStatus() != OrderStatus.SHIPPED) {
			throw new MyException("Yalnız SHIPPED statusundakı sifariş çatdırıla bilər");
		}

		order.setStatus(OrderStatus.DELIVERED);
		order.setDeliveryTime(LocalDateTime.now());

		repository.save(order);

		return convertToDTO(order);
	}

	public OrderDTO cancelOrder(UUID id, String currentUserPhone) {
		Order order = findByOrder(id);

		UserDTO currentUser = userService.findByPhoneNumber(currentUserPhone);

		boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
		boolean isCustomer = currentUser.getRole() == UserRole.CUSTOMER;

		if (!isAdmin && !isCustomer) {
			throw new MyException("Bu əməliyyatı icra etmək üçün icazəniz yoxdur");
		}

		if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
			throw new MyException("Yalnız SHIPPED  və CONFIRMED statusundakı sifariş ləğv oluna bilər");
		}

		order.setStatus(OrderStatus.CANCELLED);

		repository.save(order);

		return convertToDTO(order);
	}
}