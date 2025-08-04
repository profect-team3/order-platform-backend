package app.domain.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import app.domain.user.model.entity.QUser;
import app.domain.user.model.entity.User;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserSearchRepositoryImpl implements UserSearchRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<User> searchUser(String keyWord, Pageable pageable) {
		QUser user = QUser.user;
		BooleanBuilder builder = new BooleanBuilder();

		if (StringUtils.hasText(keyWord)) {
			builder.and(
				user.realName.containsIgnoreCase(keyWord)
					.or(user.email.containsIgnoreCase(keyWord))
					.or(user.nickname.containsIgnoreCase(keyWord))
			);
		}

		List<User> results = queryFactory
			.selectFrom(user)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(user.createdAt.desc())
			.fetch();

		long total = queryFactory
			.select(user.count())
			.from(user)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(results, pageable, total);
	}
}