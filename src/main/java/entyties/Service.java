package entyties;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "services")
public class Service {
    @Id @Column(name = "id") @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "endsubscription")
    private LocalDateTime endOfSubscription;

    public LocalDateTime getEndOfSubscription() {
        return endOfSubscription;
    }


    public void renewSubscription(Long monts){
       if (endOfSubscription==null||endOfSubscription.isBefore(LocalDateTime.now()))
            endOfSubscription=LocalDateTime.now().plusMonths(monts);
        else
            endOfSubscription=endOfSubscription.plusMonths(monts);
    }
}
