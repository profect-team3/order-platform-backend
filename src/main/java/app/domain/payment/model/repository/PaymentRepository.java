package app.domain.payment.model.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.domain.payment.model.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
	Optional<Payment> findByOrdersId(UUID ordersId);
}