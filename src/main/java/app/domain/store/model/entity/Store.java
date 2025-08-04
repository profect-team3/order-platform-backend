package app.domain.store.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import app.domain.menu.model.entity.Category;
import app.domain.store.status.StoreAcceptStatus;
import app.domain.user.model.entity.User;
import app.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Column(nullable = false, length = 100)
	private String storeName;

	@Column
	private String description;

	@Column(nullable = false)
	private String address;

	@Column(length = 20)
	private String phoneNumber;

	@Column(nullable = false)
	private long minOrderAmount = 0;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private StoreAcceptStatus storeAcceptStatus = StoreAcceptStatus.PENDING;


	public void updateAcceptStatus(StoreAcceptStatus newStatus) {
		this.storeAcceptStatus = newStatus;
	}

	public boolean isDeleted() {
		return this.deletedAt != null;
	}

	public void markAsDeleted() {
		this.deletedAt = LocalDateTime.now();
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setMinOrderAmount(long minOrderAmount) {
		this.minOrderAmount = minOrderAmount;
	}
}
