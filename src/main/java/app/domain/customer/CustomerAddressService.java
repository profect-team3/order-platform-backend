package app.domain.customer;

import app.domain.customer.dto.response.GetCustomerAddressListResponse;
import app.domain.customer.status.CustomerErrorStatus;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.UserAddress;
import app.domain.user.model.UserAddressRepository;
import app.domain.customer.dto.request.AddCustomerAddressRequest;
import app.domain.customer.dto.response.AddCustomerAddressResponse;
import app.global.SecurityUtil;
import app.global.apiPayload.exception.GeneralException;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerAddressService {

	private final UserAddressRepository userAddressRepository;
	private final SecurityUtil securityUtil;

	@Transactional(readOnly = true)
	// @PreAuthorize("hasAuthority('CUSTOMER')")
	public List<GetCustomerAddressListResponse> getCustomerAddresses() throws GeneralException {
		User user = securityUtil.getCurrentUser();

		try {
			return userAddressRepository.findAllByUserUserId(user.getUserId())
					.stream()
					.map(GetCustomerAddressListResponse::from)
					.toList();
		} catch (DataAccessException e) {
			throw new GeneralException(CustomerErrorStatus.ADDRESS_READ_FAILED);
		}
	}

	@Transactional
	// @PreAuthorize("hasAuthority('CUSTOMER')")
	public AddCustomerAddressResponse addCustomerAddress(AddCustomerAddressRequest request) {
		User user = securityUtil.getCurrentUser();

		if (userAddressRepository.existsByUserAndAddressAndAddressDetail(user, request.getAddress(), request.getAddressDetail())) {
			throw new GeneralException(CustomerErrorStatus.ADDRESS_ALREADY_EXISTS);
		}

		boolean finalIsDefault = request.isDefault();

		if (!finalIsDefault) {
			if (userAddressRepository.findAllByUserUserId(user.getUserId()).isEmpty()) {
				finalIsDefault = true;
			}
		}

		if (finalIsDefault) {
			userAddressRepository.findByUser_UserIdAndIsDefaultTrue(user.getUserId())
					.ifPresent(existingDefault -> {
						existingDefault.setDefault(false);
						userAddressRepository.save(existingDefault);
					});
		}

		UserAddress address = UserAddress.builder()
				.user(user)
				.alias(request.getAlias())
				.address(request.getAddress())
				.addressDetail(request.getAddressDetail())
				.isDefault(finalIsDefault)
				.build();

		try {
			UserAddress savedAddress = userAddressRepository.save(address);
			if (savedAddress.getAddressId() == null) {
				throw new GeneralException(CustomerErrorStatus.ADDRESS_ADD_FAILED);
			}

			return new AddCustomerAddressResponse(savedAddress.getAddressId());
		} catch (DataAccessException e) {
			throw new GeneralException(CustomerErrorStatus.ADDRESS_ADD_FAILED);
		}
	}
}
