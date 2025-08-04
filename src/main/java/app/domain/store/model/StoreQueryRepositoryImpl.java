package app.domain.store.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import app.domain.customer.dto.response.GetStoreListResponse;
import app.domain.review.model.entity.QReview;
import app.domain.store.model.entity.QStore;
import app.domain.store.status.StoreAcceptStatus;
import app.global.apiPayload.PagedResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class StoreQueryRepositoryImpl implements StoreQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public PagedResponse<GetStoreListResponse> searchStoresWithAvgRating(
		String keyword,
		StoreAcceptStatus status,
		Pageable pageable
	) {
		QStore store = QStore.store;
		QReview review = QReview.review;

		List<GetStoreListResponse> content = queryFactory
			.select(Projections.constructor(
				GetStoreListResponse.class,
				store.storeId,
				store.storeName,
				store.address,
				store.minOrderAmount,
				review.rating.avg().coalesce(0.0)
			))
			.from(store)
			.leftJoin(review).on(review.store.eq(store))
			.where(
				store.storeAcceptStatus.eq(status),
				store.deletedAt.isNull(),
				store.storeName.containsIgnoreCase(keyword)
			)
			.groupBy(store.storeId)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(store.count())
			.from(store)
			.where(
				store.storeAcceptStatus.eq(status),
				store.deletedAt.isNull(),
				store.storeName.containsIgnoreCase(keyword)
			)
			.fetchOne();

		Page<GetStoreListResponse> page = new PageImpl<>(content, pageable, total != null ? total : 0);
		return PagedResponse.from(page);
	}

	@Override
	public PagedResponse<GetStoreListResponse> getApprovedStore(Pageable pageable) {
		QStore store = QStore.store;
		QReview review = QReview.review;

		List<GetStoreListResponse> results = queryFactory
			.select(Projections.constructor(GetStoreListResponse.class,
				store.storeId,
				store.storeName,
				store.address,
				store.minOrderAmount,
				review.rating.avg().coalesce(0.0)
			))
			.from(store)
			.leftJoin(review).on(review.store.eq(store))
			.where(store.storeAcceptStatus.eq(StoreAcceptStatus.APPROVE)
				.and(store.deletedAt.isNull()))
			.groupBy(store.storeId)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 전체 개수 쿼리 (페이징용)
		Long total = queryFactory
			.select(store.countDistinct())
			.from(store)
			.where(store.storeAcceptStatus.eq(StoreAcceptStatus.APPROVE)
				.and(store.deletedAt.isNull()))
			.fetchOne();

		Page<GetStoreListResponse> page = new PageImpl<>(results, pageable, total != null ? total : 0);

		return PagedResponse.from(page);
	}

	@Override
	public PagedResponse<GetStoreListResponse> getAllStore(StoreAcceptStatus status, Pageable pageable) {
		QStore store = QStore.store;
		QReview review = QReview.review;

		List<GetStoreListResponse> content = queryFactory
			.select(Projections.constructor(
				GetStoreListResponse.class,
				store.storeId,
				store.storeName,
				store.address,
				store.minOrderAmount,
				review.rating.avg().coalesce(0.0)
			))
			.from(store)
			.leftJoin(review).on(review.store.eq(store))
			.where(
				store.storeAcceptStatus.eq(status),
				store.deletedAt.isNull()
			)
			.groupBy(store.storeId)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(store.count())
			.from(store)
			.where(
				store.storeAcceptStatus.eq(status),
				store.deletedAt.isNull()
			)
			.fetchOne();

		Page<GetStoreListResponse> page = new PageImpl<>(content, pageable, total != null ? total : 0);

		return PagedResponse.from(page);
	}
}