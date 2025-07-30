package app.domain.customer.controller;

import app.domain.customer.dto.*;
import app.domain.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
@Tag(name = "Customer API", description = "고객 주문 및 리뷰 관련 API")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/order")
    @Operation(summary = "고객 주문 내역 조회", description = "고객의 주문 내역을 조회합니다.")
    public ResponseEntity<List<CustomerOrderResponse>> getCustomerOrders(
            @RequestParam @Parameter(description = "고객 ID") Long customerId,
            @RequestParam(required = false) @Parameter(description = "특정 주문 ID") UUID orderId,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "조회 시작 날짜") LocalDateTime startDate,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @Parameter(description = "조회 종료 날짜") LocalDateTime endDate) {
        
        List<CustomerOrderResponse> orders = customerService.getCustomerOrders(
                customerId, orderId, startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/review")
    @Operation(summary = "고객 리뷰 내역 조회", description = "특정 고객의 리뷰 내역 또는 특정 상점의 리뷰 내역을 조회합니다.")
    public ResponseEntity<List<CustomerReviewResponse>> getCustomerReviews(
            @RequestParam(required = false) @Parameter(description = "고객 ID") Long customerId,
            @RequestParam(required = false) @Parameter(description = "상점 ID") UUID storeId) {
        
        if (customerId == null && storeId == null) {
            throw new IllegalArgumentException("customerId 또는 storeId 중 하나는 필수입니다.");
        }
        
        List<CustomerReviewResponse> reviews = customerService.getCustomerReviews(customerId, storeId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/review")
    @Operation(summary = "리뷰 작성", description = "고객이 상품에 대한 리뷰를 작성합니다.")
    public ResponseEntity<CreateReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request) {
        
        CreateReviewResponse response = customerService.createReview(request);
        return ResponseEntity.ok(response);
    }
}