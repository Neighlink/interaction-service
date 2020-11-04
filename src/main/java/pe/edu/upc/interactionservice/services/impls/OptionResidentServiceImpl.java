package pe.edu.upc.interactionservice.services.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.interactionservice.entities.Option;
import pe.edu.upc.interactionservice.entities.OptionResident;
import pe.edu.upc.interactionservice.repositories.OptionResidentRepository;
import pe.edu.upc.interactionservice.services.OptionResidentService;

import java.util.List;
import java.util.Optional;

@Service
public class OptionResidentServiceImpl implements OptionResidentService {

    @Autowired
    private OptionResidentRepository optionResidentRepository;

    @Override
    public OptionResident save(OptionResident entity) throws Exception {
        return optionResidentRepository.save(entity);
    }

    @Override
    public List<OptionResident> findAll() throws Exception {
        return optionResidentRepository.findAll();
    }

    @Override
    public Optional<OptionResident> findById(Long aLong) throws Exception {
        return optionResidentRepository.findById(aLong);
    }

    @Override
    public OptionResident update(OptionResident entity) throws Exception {
        return optionResidentRepository.save(entity);
    }

    @Override
    public void deleteById(Long aLong) throws Exception {
        optionResidentRepository.deleteById(aLong);
    }

    @Override
    public Optional<List<OptionResident>> findAllByOption(Option option) {
        return optionResidentRepository.findAllByOption(option);
    }
}
