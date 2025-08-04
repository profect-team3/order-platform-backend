package app.domain.order.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.domain.order.model.repository.OrdersRepository;
import app.domain.order.model.entity.Orders;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundUpdateService {

	private final OrdersRepository ordersRepository;

	@Transactional
	public void updateRefundableStatus(UUID orderId) {
		try {
			Orders order = ordersRepository.findById(orderId)
				.orElseThrow(() -> new GeneralException(ErrorStatus.ORDER_NOT_FOUND));

			order.disableRefund();
			ordersRepository.save(order);
			log.info("주문 {} 환불 불가 상태로 변경 완료", orderId);
		} catch (GeneralException e) {
			throw e;
		} catch (Exception e) {
			log.error("주문 {} 환불 불가 처리 실패", orderId, e);
			throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
		}
	}
}