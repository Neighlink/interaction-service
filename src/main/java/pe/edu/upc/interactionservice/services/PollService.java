package pe.edu.upc.interactionservice.services;

import pe.edu.upc.interactionservice.entities.Poll;

import java.util.List;
import java.util.Optional;

public interface PollService extends CrudService<Poll, Long> {
    Optional<List<Poll>> findAllByCondominiumId(Long condominiumId);
}
