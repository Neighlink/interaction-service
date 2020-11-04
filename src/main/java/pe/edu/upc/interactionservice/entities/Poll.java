package pe.edu.upc.interactionservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "polls")
@Data
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Column(nullable = false)
    private Long condominiumId;
    @Column(nullable = false)
    private Long administratorId;
    @OneToMany
    private List<Option> options;
}
