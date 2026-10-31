package dev.storylab.story_api.repository;

import dev.storylab.story_api.entity.Story;
import dev.storylab.story_api.entity.StoryStatus;
import dev.storylab.story_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story,Long> {
    //save(), findById(), findAll(), deleteById()
    List<Story> findByStatus(StoryStatus status);
    List<Story> findByStatusAndAuthor(StoryStatus status, User author);
}
