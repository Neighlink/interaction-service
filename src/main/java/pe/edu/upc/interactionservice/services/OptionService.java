package pe.edu.upc.interactionservice.services;


import pe.edu.upc.interactionservice.entities.Option;

import java.util.List;
import java.util.Optional;

public interface OptionService extends CrudService<Option, Long> {
    Optional<List<Option>> findAllByPoll(Long pollId);
}
