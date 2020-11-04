package pe.edu.upc.interactionservice.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.interactionservice.entities.Option;
import pe.edu.upc.interactionservice.repositories.OptionRepository;
import pe.edu.upc.interactionservice.services.OptionService;

import java.util.List;
import java.util.Optional;

@Service
public class OptionServiceImpl implements OptionService {

    @Autowired
    private OptionRepository optionRepository;

    @Override
    public Option save(Option entity) throws Exception {
        return optionRepository.save(entity);
    }

    @Override
    public List<Option> findAll() throws Exception {
        return optionRepository.findAll();
    }

    @Override
    public Optional<Option> findById(Long aLong) throws Exception {
        return optionRepository.findById(aLong);
    }

    @Override
    public Option update(Option entity) throws Exception {
        return optionRepository.save(entity);
    }

    @Override
    public void deleteById(Long aLong) throws Exception {
        optionRepository.deleteById(aLong);
    }

    @Override
    public Optional<List<Option>> findAllByPoll(Long pollId) {
        return optionRepository.findAllByPoll(pollId);
    }
}
