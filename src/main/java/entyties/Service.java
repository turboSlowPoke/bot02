package entyties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "services")
public class Service {
    @Id @Column(name = "id")
    private long id;
    @Column(name = "endsubscription")
    private LocalDateTime endOfSubscription;

    public LocalDateTime getEndOfSubscription() {
        return endOfSubscription;
    }
}
