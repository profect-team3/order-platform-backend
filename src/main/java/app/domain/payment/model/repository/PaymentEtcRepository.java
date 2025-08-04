package app.domain.payment.model.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.domain.payment.model.entity.PaymentEtc;

@Repository
public interface PaymentEtcRepository extends JpaRepository<PaymentEtc, UUID> {
}