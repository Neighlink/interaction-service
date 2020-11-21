package pe.edu.upc.interactionservice.services;

import pe.edu.upc.interactionservice.entities.Option;
import pe.edu.upc.interactionservice.entities.OptionResident;

import java.util.List;
import java.util.Optional;

public interface OptionResidentService extends CrudService<OptionResident, Long> {
    List<OptionResident> findAllByOption(Long optionId);
}
