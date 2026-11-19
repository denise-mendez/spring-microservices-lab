package dev.storylab.storyservice.repository;

import dev.storylab.storyservice.entity.Story;
import dev.storylab.storyservice.entity.StoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story,Long> {
    //save(), findById(), findAll(), deleteById()
    List<Story> findByStatus(StoryStatus status);
    List<Story> findByStatusAndAuthorId(StoryStatus status, Long authorId);
}