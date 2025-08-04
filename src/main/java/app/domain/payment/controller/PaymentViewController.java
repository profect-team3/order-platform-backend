package app.domain.payment.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentViewController {

	@Value("${TOSS_CLIENT_KEY}")
	private String tossClientKey;

	@GetMapping("/checkout")
	public String checkout(@RequestParam String orderId,
		@RequestParam Long totalPrice,
		Model model) {
		model.addAttribute("orderId", orderId);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("tossClientKey", tossClientKey);

		return "checkout";
	}

	@GetMapping("/success")
	public String success(@RequestParam String paymentKey,
		@RequestParam String orderId,
		@RequestParam String amount,
		Model model) {
		model.addAttribute("paymentKey", paymentKey);
		model.addAttribute("orderId", orderId);
		model.addAttribute("amount", amount);
		return "success";
	}

	@GetMapping("/fail")
	public String fail(@RequestParam String code,
		@RequestParam String message,
		@RequestParam String orderId,
		Model model) {
		model.addAttribute("code", code);
		model.addAttribute("message", message);
		model.addAttribute("orderId", orderId);
		return "fail";
	}
}