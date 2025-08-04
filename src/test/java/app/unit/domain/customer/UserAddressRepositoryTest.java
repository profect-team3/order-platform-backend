package app.unit.domain.customer;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.context.annotation.Import;

import app.domain.user.model.UserAddressRepository;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.UserAddress;
import app.domain.user.model.entity.enums.UserRole;
import app.global.config.JpaAuditingConfig;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@DisplayName("UserAddressRepository 테스트")
class UserAddressRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserAddressRepository userAddressRepository;

	private User user;
	private UserAddress defaultAddress;
	private UserAddress anotherAddress;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.username("testuser")
			.password("password123!")
			.email("test@example.com")
			.nickname("testnick")
			.realName("김테스트")
			.phoneNumber("01012345678")
			.userRole(UserRole.CUSTOMER)
			.build();
		entityManager.persist(user);

		defaultAddress = UserAddress.builder()
			.user(user)
			.alias("우리집")
			.address("서울시 강남구")
			.addressDetail("101호")
			.isDefault(true)
			.build();
		entityManager.persist(defaultAddress);

		anotherAddress = UserAddress.builder()
			.user(user)
			.alias("회사")
			.address("서울시 서초구")
			.addressDetail("202호")
			.isDefault(false)
			.build();
		entityManager.persist(anotherAddress);

		entityManager.flush();
	}

	@Nested
	@DisplayName("findAllByUserUserId 메서드 테스트")
	class FindAllByUserUserIdTest {
		@Test
		@DisplayName("성공: 존재하는 userId로 조회 시, 해당 유저의 주소 목록을 반환한다.")
		void findAllByUserUserId_ExistingUser_ReturnsAddressList() {
			// when
			List<UserAddress> foundAddresses = userAddressRepository.findAllByUserUserId(user.getUserId());

			// then
			assertThat(foundAddresses).hasSize(2);
			assertThat(foundAddresses).extracting("alias").containsExactlyInAnyOrder("우리집", "회사");
		}

		@Test
		@DisplayName("실패: 존재하지 않는 userId로 조회 시, 빈 목록을 반환한다.")
		void findAllByUserUserId_NonExistingUser_ReturnsEmptyList() {
			// when
			List<UserAddress> foundAddresses = userAddressRepository.findAllByUserUserId(999L);

			// then
			assertThat(foundAddresses).isEmpty();
		}

		@Test
		@DisplayName("실패: 주소가 없는 기존 사용자로 조회 시, 빈 목록을 반환한다.")
		void findAllByUserUserId_ExistingUserWithoutAddresses_ReturnsEmptyList() {
			// given
			User userWithoutAddress = User.builder()
				.username("nouser")
				.password("pass")
				.email("no@email.com")
				.nickname("nousernick")
				.realName("김노유저")
				.phoneNumber("01000000000")
				.userRole(UserRole.CUSTOMER)
				.build();
			entityManager.persistAndFlush(userWithoutAddress);

			// when
			List<UserAddress> foundAddresses = userAddressRepository.findAllByUserUserId(
				userWithoutAddress.getUserId());

			// then
			assertThat(foundAddresses).isEmpty();
		}
	}

	@Nested
	@DisplayName("existsBy... 메서드 테스트")
	class ExistsByMethodsTest {
		@Test
		@DisplayName("성공: 존재하는 주소 정보에 대해 true를 반환한다.")
		void existsByUserAndAddressAndAddressDetail_Existing_ReturnsTrue() {
			// when
			boolean exists = userAddressRepository.existsByUserAndAddressAndAddressDetail(user, "서울시 강남구", "101호");

			// then
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("실패: 존재하지 않는 주소 정보에 대해 false를 반환한다.")
		void existsByUserAndAddressAndAddressDetail_NonExisting_ReturnsFalse() {
			// when
			boolean exists = userAddressRepository.existsByUserAndAddressAndAddressDetail(user, "경기도 성남시", "분당구");

			// then
			assertThat(exists).isFalse();
		}
	}

	@Nested
	@DisplayName("findByUserAndIsDefaultTrue 메서드 테스트")
	class FindByUserAndIsDefaultTrueTest {
		@Test
		@DisplayName("성공: 기본 주소가 있는 사용자로 조회 시, 기본 주소 객체를 담은 Optional을 반환한다.")
		void findByUserAndIsDefaultTrue_UserWithDefaultAddress_ReturnsOptionalOfAddress() {
			// when
			Optional<UserAddress> foundDefaultAddress = userAddressRepository.findByUser_UserIdAndIsDefaultTrue(
				user.getUserId());

			// then
			assertThat(foundDefaultAddress).isPresent();
			assertThat(foundDefaultAddress.get().getAlias()).isEqualTo("우리집");
			assertThat(foundDefaultAddress.get().isDefault()).isTrue();
		}

		@Test
		@DisplayName("실패: 기본 주소가 없는 사용자로 조회 시, 빈 Optional을 반환한다.")
		void findByUserAndIsDefaultTrue_UserWithoutDefaultAddress_ReturnsEmptyOptional() {
			// given
			// 모든 주소를 기본 주소가 아니도록 변경
			defaultAddress.setDefault(false);
			entityManager.persistAndFlush(defaultAddress);

			// when
			Optional<UserAddress> foundDefaultAddress = userAddressRepository.findByUser_UserIdAndIsDefaultTrue(
				user.getUserId());

			// then
			assertThat(foundDefaultAddress).isNotPresent();
		}
	}

	@Nested
	@DisplayName("저장 및 제약조건 테스트")
	class SavingAndConstraintTest {

		@Test
		@DisplayName("성공: User에 새로운 주소를 저장하고 관계가 올바르게 매핑된다.")
		void save_NewAddressForUser_Succeeds() {
			// given
			UserAddress newAddress = UserAddress.builder()
				.user(user)
				.alias("새로운 집")
				.address("경기도 성남시 분당구")
				.addressDetail("202호")
				.isDefault(false)
				.build();

			// when
			UserAddress savedAddress = userAddressRepository.save(newAddress);
			entityManager.flush();
			entityManager.clear();

			// then
			UserAddress foundAddress = entityManager.find(UserAddress.class, savedAddress.getAddressId());
			assertThat(foundAddress).isNotNull();
			assertThat(foundAddress.getAddressId()).isNotNull();
			assertThat(foundAddress.getUser().getUserId()).isEqualTo(user.getUserId());
			assertThat(foundAddress.getAlias()).isEqualTo("새로운 집");
			assertThat(foundAddress.isDefault()).isFalse();
		}

		@Test
		@DisplayName("예외: user(외래키)가 null이면 DataIntegrityViolationException이 발생한다.")
		void save_NullUser_ThrowsDataIntegrityViolationException() {
			// given
			UserAddress addressWithNullUser = UserAddress.builder()
				.user(null)
				.alias("주인 없는 집")
				.address("어딘가")
				.addressDetail("202호")
				.isDefault(false)
				.build();

			// when & then
			assertThatThrownBy(() -> userAddressRepository.saveAndFlush(addressWithNullUser))
				.isInstanceOf(DataIntegrityViolationException.class);
		}

		@Test
		@DisplayName("예외: alias가 null이면 DataIntegrityViolationException이 발생한다.")
		void save_NullAlias_ThrowsDataIntegrityViolationException() {
			// given
			UserAddress addressWithNullAlias = UserAddress.builder()
				.user(user)
				.alias(null)
				.address("어딘가")
				.addressDetail("202호")
				.isDefault(false)
				.build();

			// when & then
			assertThatThrownBy(() -> userAddressRepository.saveAndFlush(addressWithNullAlias))
				.isInstanceOf(DataIntegrityViolationException.class);
		}

		@Test
		@DisplayName("예외: address가 null이면 DataIntegrityViolationException이 발생한다.")
		void save_NullAddress_ThrowsDataIntegrityViolationException() {
			// given
			UserAddress addressWithNullAddress = UserAddress.builder()
				.user(user)
				.alias("주인 없는 집")
				.address(null)
				.addressDetail("202호")
				.isDefault(false)
				.build();

			// when & then
			assertThatThrownBy(() -> userAddressRepository.saveAndFlush(addressWithNullAddress))
				.isInstanceOf(DataIntegrityViolationException.class);
		}

		@Test
		@DisplayName("예외: addressDetail가 null이면 DataIntegrityViolationException이 발생한다.")
		void save_NullAddressDetail_ThrowsDataIntegrityViolationException() {
			// given
			UserAddress addressWithNullAddressDetail = UserAddress.builder()
				.user(user)
				.alias("주인 없는 집")
				.address("어딘가")
				.addressDetail(null)
				.isDefault(false)
				.build();

			// when & then
			assertThatThrownBy(() -> userAddressRepository.saveAndFlush(addressWithNullAddressDetail))
				.isInstanceOf(DataIntegrityViolationException.class);
		}
	}
}