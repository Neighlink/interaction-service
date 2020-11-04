package pe.edu.upc.interactionservice.services;

import pe.edu.upc.interactionservice.entities.News;

import java.util.List;
import java.util.Optional;

public interface NewsService extends CrudService<News, Long> {
    Optional<List<News>> findAllByCondominiumId(Long condominiumId);
}
