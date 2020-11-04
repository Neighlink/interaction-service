package pe.edu.upc.interactionservice.models;

import lombok.Data;
import pe.edu.upc.interactionservice.entities.*;

import java.util.List;

@Data
public class OptionReplyModel {
    private List<OptionResident>  optionResidents;
    private Option option;
}
