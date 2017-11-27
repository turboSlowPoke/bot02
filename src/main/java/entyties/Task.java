package entyties;

import org.apache.log4j.Logger;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task implements Serializable {
    @Transient
    private static final Logger log = Logger.getLogger(Task.class);
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String type;
    private String status;
   // @ManyToMany(fetch = FetchType.EAGER) @Size(max = 2)
   // private List<User> users;
    private LocalDateTime dateTimeOpening;
    private LocalDateTime dateTimeEnding;
    @OneToOne
    private User customer;
    @OneToOne
    private User admin;

    public Task() {
    }

    public Task(String type, User customer) {
        this.type = type;
        this.status = TaskStatus.OPEN;
        this.customer = customer;
        this.dateTimeOpening = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDateTimeOpening() {
        return dateTimeOpening;
    }

    public LocalDateTime getDateTimeEnding() {
        return dateTimeEnding;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public void setDateTimeEnding(LocalDateTime dateTimeEnding) {
        this.dateTimeEnding = dateTimeEnding;
    }

    @Override
    public String toString() {
           return "Id заявки: "+this.id
                +"\nТип: "+this.type
                +"\nДата создания: "+this.dateTimeOpening
                +"\nCustomer: "+this.customer
                +"\nAdmin: "+this.admin;

    }
}
