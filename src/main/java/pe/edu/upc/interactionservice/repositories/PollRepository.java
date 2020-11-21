package pe.edu.upc.interactionservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.interactionservice.entities.Poll;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    @Query("SELECT p FROM Poll p WHERE p.condominiumId = :condominiumId AND p.isDelete = false")
    Optional<List<Poll>> findAllByCondominiumId(@Param("condominiumId") Long condominiumId);
}
