package pe.edu.upc.interactionservice.models;

import lombok.Data;
import pe.edu.upc.interactionservice.entities.*;

import java.util.List;

@Data
public class ResponsePollResident {
    private Poll poll;
    private List<OptionReplyModel> optionReplies;
}
