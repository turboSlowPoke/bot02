package entyties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LocalTransactions")
public class BonusTransaction {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private LocalDateTime dateTime;
    @Column(scale = 2,precision = 10)
    private BigDecimal amount;
    @OneToOne
    private User user;

    public BonusTransaction() {
    }

    public BonusTransaction( BigDecimal amount, User user) {
        this.dateTime = LocalDateTime.now();
        this.amount = amount;
        this.user =user;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public User getUser() {
        return user;
    }
}
