package dev.storylab.story_api.repository;

import dev.storylab.story_api.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story,Long> {
    //save(), findById(), findAll(), deleteById()
}
