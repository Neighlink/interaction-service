package pe.edu.upc.interactionservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.interactionservice.entities.Poll;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
}
