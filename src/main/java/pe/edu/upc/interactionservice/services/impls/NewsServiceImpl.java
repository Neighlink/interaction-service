package pe.edu.upc.interactionservice.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.interactionservice.entities.News;
import pe.edu.upc.interactionservice.repositories.NewsRepository;
import pe.edu.upc.interactionservice.services.NewsService;

import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Override
    public News save(News entity) throws Exception {
        return newsRepository.save(entity);
    }

    @Override
    public List<News> findAll() throws Exception {
        return newsRepository.findAll();
    }

    @Override
    public Optional<News> findById(Long aLong) throws Exception {
        return newsRepository.findById(aLong);
    }

    @Override
    public News update(News entity) throws Exception {
        return newsRepository.save(entity);
    }

    @Override
    public void deleteById(Long aLong) throws Exception {
        newsRepository.deleteById(aLong);
    }

    @Override
    public Optional<List<News>> findAllByCondominiumId(Long condominiumId) {
        return newsRepository.findAllByCondominiumId(condominiumId);
    }
}
