package app.domain.menu.model.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.domain.menu.model.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
