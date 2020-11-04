package pe.edu.upc.interactionservice.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.interactionservice.entities.Poll;
import pe.edu.upc.interactionservice.repositories.PollRepository;
import pe.edu.upc.interactionservice.services.PollService;

import java.util.List;
import java.util.Optional;

@Service
public class PollServiceImpl implements PollService {

    @Autowired
    private PollRepository pollRepository;

    @Override
    public Poll save(Poll entity) throws Exception {
        return pollRepository.save(entity);
    }

    @Override
    public List<Poll> findAll() throws Exception {
        return pollRepository.findAll();
    }

    @Override
    public Optional<Poll> findById(Long aLong) throws Exception {
        return pollRepository.findById(aLong);
    }

    @Override
    public Poll update(Poll entity) throws Exception {
        return pollRepository.save(entity);
    }

    @Override
    public void deleteById(Long aLong) throws Exception {
        pollRepository.deleteById(aLong);
    }
}
