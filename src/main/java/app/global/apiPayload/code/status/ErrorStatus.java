package app.global.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

	// For test
	TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

	// USER
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "GLOBAL001", "존재하지 않는 사용자입니다."),

	// 가장 일반적인 응답
	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
	NOTICE_NOT_FOUND(HttpStatus.BAD_REQUEST, "NOTICE400", "공지사항을 찾을 수 없습니다."),

	// Store 관련
	STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE004", "해당 가맹점을 찾을 수 없습니다."),
	STORE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE_001", "존재하지 않는 매장 카테고리입니다."),
	REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE002", "존재하지 않는 지역입니다."),
	MERCHANT_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE003", "존재하지 않는 가맹점입니다."),
	INVALID_STORE_STATUS(HttpStatus.NOT_FOUND, "STORE005", "상태명이 올바르지 않습니다."),
	INVALID_STATUS_CHANGE(HttpStatus.BAD_REQUEST, "STORE006", "이미 변경이 완료 되었습니다."),

	// 인증 관련
	INVALID_PASSWORD(HttpStatus.FORBIDDEN, "AUTH_001", "비밀번호가 일치하지 않습니다."),

	// Wallet 관련
	USER_WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "WALLET_001", "사용자 지갑이 존재하지 않습니다."),
	MERCHANT_WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "WALLET_002", "가맹점 지갑이 존재하지 않습니다."),
	INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "WALLET_003", "토큰/바우처 잔액이 부족합니다."),
	INSUFFICIENT_TOKEN_BALANCE(HttpStatus.BAD_REQUEST, "WALLET_004", "토큰 잔액이 부족합니다."),

	// Transaction 관련
	TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "TX_001", "해당 거래 내역이 존재하지 않습니다."),

	// 중복결제 관련
	DUPLICATE_REQUEST(HttpStatus.CONFLICT, "DUPLICATED", "중복 요청입니다."),

	// Voucher 관련
	VOUCHER_NOT_FOUND(HttpStatus.NOT_FOUND, "VOUCHER_001", "존재하지 않는 바우처입니다."),
	VOUCHER_ALREADY_USED(HttpStatus.BAD_REQUEST, "VOUCHER_002", "이미 사용된 바우처입니다."),
	VOUCHER_EXPIRED(HttpStatus.BAD_REQUEST, "VOUCHER_003", "만료된 바우처입니다."),
	VOUCHER_OWNERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "VOUCHER_004", "해당 사용자의 바우처 소유 정보가 없습니다."),
	VOUCHER_SOLD_OUT(HttpStatus.BAD_REQUEST, "VOUCHER_005", "해당 바우처는 모두 소진되었습니다."),
	VOUCHER_MERCHANT_NOT_MATCH(HttpStatus.BAD_REQUEST, "VOUCHER_006", "바우처의 사용처가 일치하지 않습니다."),
	VOUCHER_STORE_NOT_USABLE(HttpStatus.BAD_REQUEST, "VOUCHER_007", "해당 매장에서 바우처를 사용할 수 없습니다."),
	VOUCHER_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN, "VOUCHER_008", "이 바우처는 해당 사용자의 소유가 아닙니다."),
	VOUCHER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "VOUCHER_009", "이미 삭제 처리된 바우처입니다."),

	// Merchant 관련
	MERCHANT_ALREADY_EXISTS(HttpStatus.CONFLICT, "MERCHANT_001", "이미 존재하는 가맹점주입니다."),
	MERCHANT_PASSWORD_UPDATE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "MERCHANT_002", "현재 비밀번호와 새 비밀번호를 모두 입력해야 합니다."),
	MERCHANT_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "MERCHANT_003", "현재 비밀번호가 일치하지 않습니다."),

	// Notification 관련
	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_001", "해당 알림을 찾을 수 없습니다."),
	NOTIFICATION_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_SETTING_001", "알림 설정 정보가 존재하지 않습니다."),

	// Email 관련
	EMAIL_NOT_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SEND_001", "이메일 전송 실패, 관리자에게 문의 바랍니다."),
	EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "EMAIL_SEND_002", "이메일 전송 실패, 가입되지 않은 이메일입니다."),
	VERIFY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_VERIFY_001", "이메일 인증 실패, 재시도 바랍니다."),
	EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "EMAIL_VERIFY_002", "이메일 인증이 완료되지 않았습니다."),
	EMAIL_CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_CREATE_001", "이메일 생성에 실패했습니다."),

	// ocr 관련
	OCR_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OCR_001", "OCR 처리에 실패했습니다."),

	// Map 관련
	ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "MAP_001", "존재하지 않는 주소입니다."),
	INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "MAP_002", "유효하지 않은 주소입니다. 카카오 주소 검색 실패."),
	INVALID_RADIUS(HttpStatus.BAD_REQUEST, "MAP_003", "유효하지 않은 반경입니다."),
	INVALID_LATITUDE(HttpStatus.BAD_REQUEST, "MAP_004", "유효하지 않은 좌표입니다."),
	INVALID_SIDO(HttpStatus.NOT_FOUND, "MAP_005", "유효하지 않은 도/시입니다."),

	// Pagination 관련
	INVALID_PAGE(HttpStatus.BAD_REQUEST, "PAGE_400", "유효하지 않은 페이지 넘버입니다."),

	// Cart 관련
	CART_REDIS_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CART001", "장바구니 Redis 저장에 실패했습니다."),
	CART_REDIS_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CART002", "장바구니 Redis 조회에 실패했습니다."),
	CART_ITEM_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CART003", "장바구니 아이템 파싱에 실패했습니다."),
	CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART004", "장바구니를 찾을 수 없습니다."),
	CART_DB_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CART005", "장바구니 DB 동기화에 실패했습니다."),
	INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "CART006", "수량은 1 이상이어야 합니다."),

	// Order 관련
	MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU000", "메뉴를 찾을 수 없습니다."),
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER000", "주문을 찾을 수 없습니다."),

	// Payment 관련
	PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT001", "결제 승인 정보가 결제 요청할 때의 정보와 다릅니다."),
	PAYMENT_CONFIRM_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT002", "결제 승인에 실패했습니다."),
	TOSS_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT003", "토스페이먼츠 API 오류가 발생했습니다."),
	PAYMENT_NOT_REFUNDABLE(HttpStatus.BAD_REQUEST, "PAYMENT004", "환불이 불가능한 주문입니다."),
	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT005", "결제내역을 찾을 수 없습니다."),
	INVALID_ORDER_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "ORDER007", "유효하지 않은 주문 상태 전이입니다."),
	ORDER_CANCEL_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "ORDER009", "주문 취소 가능 시간이 초과되었습니다."),

	// 스마트컨트랙트 (Token) 관련
	TOKEN_TRANSFER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_001", "스마트컨트랙트 전송 중 오류가 발생했습니다."),
	TOKEN_MINT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_002", "토큰 발행(mint) 처리 중 오류가 발생했습니다."),
	TOKEN_BALANCE_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_003", "스마트컨트랙트 잔액 조회 중 오류가 발생했습니다."),
	TOKEN_BURN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_004", "토큰 소각(burn) 처리 중 오류가 발생했습니다."),
	BALANCE_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_005", "스마트컨트랙트와 DB의 토큰 잔액이 일치하지 않습니다."),
	BALANCE_VERIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_005", "스마트컨트랙트 잔액 조회 중 오류가 발생했습니다."),

	// address 관련
	ADDRESS_ALREADY_EXISTS(HttpStatus.CONFLICT, "ADDRESS_001", "이미 존재하는 주소입니다."),
	ADDRESS_ADD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADDRESS_002", "주소 등록에 실패했습니다."),
	ADDRESS_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADDRESS_003", "주소 목록 조회에 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ReasonDTO getReason() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.build();
	}

	@Override
	public ReasonDTO getReasonHttpStatus() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.httpStatus(httpStatus)
			.build();
	}
}