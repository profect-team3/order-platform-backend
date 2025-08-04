package app.domain.order;

import java.util.UUID;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import app.domain.order.service.RefundUpdateService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundDisableProcessor implements CommandLineRunner {

	private final RedissonClient redissonClient;
	private final RefundUpdateService refundUpdateService;
	private static final String REFUND_DISABLE_QUEUE = "refund-disable-queue";
	private Thread processingThread;

	@Override
	public void run(String... args) {
		processRefundDisableQueue();
	}

	private void processRefundDisableQueue() {
		processingThread = new Thread(() -> {
			RBlockingQueue<UUID> queue = redissonClient.getBlockingQueue(REFUND_DISABLE_QUEUE);

			while (!Thread.currentThread().isInterrupted()) {
				try {
					UUID orderId = queue.take();
					refundUpdateService.updateRefundableStatus(orderId);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				} catch (Exception e) {
					if (!Thread.currentThread().isInterrupted()) {
						log.error("환불 불가 처리 중 오류 발생", e);
					}
				}
			}
		});
		processingThread.start();
	}

	@PreDestroy
	public void shutdown() {
		if (processingThread != null && processingThread.isAlive()) {
			processingThread.interrupt();
			try {
				processingThread.join(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}