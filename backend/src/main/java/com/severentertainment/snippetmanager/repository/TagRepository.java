package com.severentertainment.snippetmanager.repository;

import com.severentertainment.snippetmanager.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
    * Custom query method to find a Tag by its name, ignoring case.
    *
    * @param name The name of the tag to find.
    * @return An Optional containing the Tag if found, or an empty Optional if not.
    */
    Optional<Tag> findByNameIgnoreCase(String name);

}
