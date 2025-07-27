package app.global.apiPayload;

import java.util.List;

import org.springframework.data.domain.Page;

public record PagedResponse<T>(
	List<T> content,
	int page,
	int size,
	long totalElements,
	int totalPages,
	boolean last
) {
	public static <T> PagedResponse<T> from(Page<T> pageData) {
		return new PagedResponse<>(
			pageData.getContent(),
			pageData.getNumber(),
			pageData.getSize(),
			pageData.getTotalElements(),
			pageData.getTotalPages(),
			pageData.isLast()
		);
	}
}