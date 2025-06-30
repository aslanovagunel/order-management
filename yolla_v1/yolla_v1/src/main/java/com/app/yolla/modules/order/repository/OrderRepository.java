package com.app.yolla.modules.order.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.yolla.modules.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

	@Query(value = "select * from orders where user_id=?1 limit ?2,?3", nativeQuery = true)
	List<Order> getAll(UUID id, Integer begin, Integer length);

	@Query(value = "select * from orders where user_id=?1 ", nativeQuery = true)
	List<Order> findOrder(UUID id);

}