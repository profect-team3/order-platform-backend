package app.domain.store.model.entity;

import java.util.UUID;

import app.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_region")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Region extends BaseEntity {

	@Id
	@GeneratedValue
	private UUID regionId;

	@Column(unique = true, nullable = false)
	private String regionCode;

	@Column(nullable = false, length = 100)
	private String regionName;

	@Column(nullable = false)
	private boolean isActive = false;

	@Column(nullable = false)
	private String fullName;

	@Column(length = 20)
	private String sido;

	@Column(length = 30)
	private String sigungu;

	@Column(length = 30)
	private String eupmyendong;
}