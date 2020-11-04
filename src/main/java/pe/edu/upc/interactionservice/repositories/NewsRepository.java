package pe.edu.upc.interactionservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.interactionservice.entities.News;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n WHERE n.condominiumId = :condominiumId")
    Optional<List<News>> findAllByCondominiumId(@Param("condominiumId") Long condominiumId);
}
