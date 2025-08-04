package app.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import app.domain.user.model.entity.User;

public interface UserSearchRepository {

	Page<User> searchUser(String keyWord, Pageable pageable);
}
