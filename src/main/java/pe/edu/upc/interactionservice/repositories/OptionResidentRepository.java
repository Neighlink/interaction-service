package pe.edu.upc.interactionservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.interactionservice.entities.Option;
import pe.edu.upc.interactionservice.entities.OptionResident;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionResidentRepository extends JpaRepository<OptionResident, Long> {
    @Query("SELECT o FROM OptionResident o WHERE o.option = :option")
    Optional<List<OptionResident>> findAllByOption(@Param("option") Option option);
}
