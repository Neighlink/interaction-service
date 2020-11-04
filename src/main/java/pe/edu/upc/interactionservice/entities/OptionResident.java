package pe.edu.upc.interactionservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "option_resident")
@Data
public class OptionResident {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "optionId", nullable = true)
    private Option option;
    @Column(nullable = false)
    private Long residentId;
    @Column(nullable = true)
    private String comment;
}
