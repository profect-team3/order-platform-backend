package app.domain.user.model;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.domain.user.model.entity.User;
import app.domain.user.model.entity.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String userName);

	Optional<User> findByUserId(Long userId);

	Page<User> findAllByUserRole(UserRole role, Pageable pageable);

	// TODO : QUERYDSL로 수정 필요
	@Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email OR u.nickname = :nickname OR u.phoneNumber = :phoneNumber")
	Optional<User> findFirstByUniqueFields(
		@Param("username") String username,
		@Param("email") String email,
		@Param("nickname") String nickname,
		@Param("phoneNumber") String phoneNumber
	);

}