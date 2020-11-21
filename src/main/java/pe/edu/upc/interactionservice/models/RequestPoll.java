package pe.edu.upc.interactionservice.models;

import lombok.Data;
import pe.edu.upc.interactionservice.entities.Option;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
public class RequestPoll {
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private List<Option> options;
}
