package dev.storylab.story_api.repository;

import dev.storylab.story_api.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Story, Long> {
    Optional<Story> findByUsername(String username);

    boolean existsByUsername(String username);
}
