package pe.edu.upc.interactionservice.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "news")
@Data
public class News {
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
    private Date date;
    @Column(nullable = false)
    private Long condominiumId;
}
