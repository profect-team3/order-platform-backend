package app.unit.domain.customer;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.enums.UserRole;

@DataJpaTest
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TestEntityManager entityManager;

	private User existingUser;

	@BeforeEach
	void setUp() {

		entityManager.clear();

		existingUser = User.builder()
			.username("existinguser")
			.password("password123")
			.email("existing@example.com")
			.nickname("existingnick")
			.realName("기존사용자")
			.phoneNumber("010-1111-1111")
			.userRole(UserRole.CUSTOMER)
			.build();

		userRepository.save(existingUser);
		entityManager.flush(); // DB에 즉시 반영
	}

	@Nested
	@DisplayName("findFirstByUniqueFields 메서드 테스트")
	class FindFirstByUniqueFields {

		@Test
		@DisplayName("성공 - 중복된 username으로 사용자 조회")
		void findFirstByUniqueFields_WithDuplicateUsername_ShouldReturnUser() {
			// given
			String duplicateUsername = "existinguser";
			String newEmail = "new@example.com";
			String newNickname = "newnick";
			String newPhoneNumber = "010-2222-2222";

			// when
			Optional<User> foundUser = userRepository.findFirstByUniqueFields(
				duplicateUsername, newEmail, newNickname, newPhoneNumber
			);

			// then
			assertThat(foundUser).isPresent();
			assertThat(foundUser.get().getUsername()).isEqualTo(existingUser.getUsername());
		}

		@Test
		@DisplayName("성공 - 중복된 email로 사용자 조회")
		void findFirstByUniqueFields_WithDuplicateEmail_ShouldReturnUser() {
			// given
			String newUsername = "newuser";
			String duplicateEmail = "existing@example.com";
			String newNickname = "newnick";
			String newPhoneNumber = "010-2222-2222";

			// when
			Optional<User> foundUser = userRepository.findFirstByUniqueFields(
				newUsername, duplicateEmail, newNickname, newPhoneNumber
			);

			// then
			assertThat(foundUser).isPresent();
			assertThat(foundUser.get().getEmail()).isEqualTo(existingUser.getEmail());
		}

		@Test
		@DisplayName("성공 - 중복된 nickname으로 사용자 조회")
		void findFirstByUniqueFields_WithDuplicateNickname_ShouldReturnUser() {
			// given
			String newUsername = "newuser";
			String newEmail = "new@example.com";
			String duplicateNickname = "existingnick";
			String newPhoneNumber = "010-2222-2222";

			// when
			Optional<User> foundUser = userRepository.findFirstByUniqueFields(
				newUsername, newEmail, duplicateNickname, newPhoneNumber
			);

			// then
			assertThat(foundUser).isPresent();
			assertThat(foundUser.get().getNickname()).isEqualTo(existingUser.getNickname());
		}

		@Test
		@DisplayName("성공 - 중복된 phoneNumber로 사용자 조회")
		void findFirstByUniqueFields_WithDuplicatePhoneNumber_ShouldReturnUser() {
			// given
			String newUsername = "newuser";
			String newEmail = "new@example.com";
			String newNickname = "newnick";
			String duplicatePhoneNumber = "010-1111-1111";

			// when
			Optional<User> foundUser = userRepository.findFirstByUniqueFields(
				newUsername, newEmail, newNickname, duplicatePhoneNumber
			);

			// then
			assertThat(foundUser).isPresent();
			assertThat(foundUser.get().getPhoneNumber()).isEqualTo(existingUser.getPhoneNumber());
		}

		@Test
		@DisplayName("성공 - 중복된 필드가 없을 경우 빈 Optional 반환")
		void findFirstByUniqueFields_WithNoDuplicates_ShouldReturnEmpty() {
			// given
			String newUsername = "newuser";
			String newEmail = "new@example.com";
			String newNickname = "newnick";
			String newPhoneNumber = "010-2222-2222";

			// when
			Optional<User> foundUser = userRepository.findFirstByUniqueFields(
				newUsername, newEmail, newNickname, newPhoneNumber
			);

			// then
			assertThat(foundUser).isNotPresent();
		}

		@Test
		@DisplayName("성공 - 여러 필드가 중복될 경우 사용자 조회 (OR 조건 테스트)")
		void findFirstByUniqueFields_WithMultipleDuplicates_ShouldReturnUser() {
			// given
			User anotherUser = User.builder()
				.username("anotheruser")
				.password("password456")
				.email("another@example.com")
				.nickname("anothernick")
				.realName("다른사용자")
				.phoneNumber("010-3333-3333")
				.userRole(UserRole.CUSTOMER)
				.build();
			userRepository.save(anotherUser);
			entityManager.flush();

			String duplicateUsername = "existinguser";
			String duplicateEmail = "another@example.com";
			String newNickname = "newnick";
			String newPhoneNumber = "010-2222-2222";

			// when
			Optional<User> foundUser = userRepository.findFirstByUniqueFields(
				duplicateUsername, duplicateEmail, newNickname, newPhoneNumber
			);

			// then
			assertThat(foundUser).isPresent();
		}
	}
}