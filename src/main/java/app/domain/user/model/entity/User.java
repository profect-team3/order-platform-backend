package app.domain.user.model.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import app.domain.user.model.entity.enums.UserRole;
import app.global.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user")
@Schema(description = "사용자 테이블")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@SQLDelete(sql = "UPDATE p_user SET deleted_at = NOW() WHERE user_id = ?")
@Where(clause = "deleted_at IS NULL")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "사용자 고유 ID", example = "1")
	private Long userId;

	@Column(nullable = false, unique = true, length = 50)
	@Schema(description = "사용자 ID")
	private String username;

	@Column(nullable = false, unique = true, length = 100)
	@Schema(description = "이메일 주소")
	private String email;

	@Column(nullable = false, length = 255)
	@Schema(description = "암호화된 비밀번호")
	private String password;

	@Column(nullable = false, unique = true, length = 50)
	@Schema(description = "닉네임")
	private String nickname;

	@Column(nullable = false, length = 50)
	@Schema(description = "실명")
	private String realName;

	@Column(nullable = false, unique = true, length = 20)
	@Schema(description = "전화번호")
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	@Schema(description = "사용자 역할(UserRole관련)")
	private UserRole userRole;

	/**
	 * 회원 탈퇴 시 개인정보를 익명화하는 메서드
	 * userRole은 유지하여 통계 등에 활용
	 */
	public void anonymizeForWithdrawal() {
		this.username = "withdrawn_user_" + this.userId;
		this.password = "withdrawn_password"; // 더 이상 로그인 불가
		this.email = "withdrawn_" + this.userId + "@example.com";
		this.nickname = "탈퇴한 사용자";
		this.realName = "탈퇴한 사용자";
		this.phoneNumber = "000-0000-0000";
	}
}