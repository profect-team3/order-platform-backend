package app.global.apiPayload;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

	private List<T> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;
	private boolean last;

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