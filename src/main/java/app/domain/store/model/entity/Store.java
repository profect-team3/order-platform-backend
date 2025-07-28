package app.domain.store.model.entity;

import java.util.UUID;

import app.domain.menu.model.entity.Category;
import app.domain.user.model.entity.User;
import app.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

	@Id
	@GeneratedValue
	private UUID storeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id", nullable = false)
	private Region region;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Column(nullable = false, length = 100)
	private String storeName;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private String address;

	@Column(length = 20)
	private String phoneNumber;

	@Column(nullable = false)
	private int minOrderAmount = 0;

	@Column(nullable = false)
	private boolean storeAcceptStatus = false;
}