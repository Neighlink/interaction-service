package pe.edu.upc.interactionservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.interactionservice.entities.Option;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    @Query("SELECT o FROM Option o WHERE o.pollId = :pollId")
    Optional<List<Option>> findAllByPoll(@Param("pollId") Long pollId);
}
