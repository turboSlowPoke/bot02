package entyties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "personaldata")
public class PersonalData {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    @Column(name = "telegramusername")
    private String telegramUsername;
    @Column(name = "startdate")
    private LocalDateTime startDate;
    @Column(name = "advcash")
    private String advcashcom;

    public PersonalData() {
    }

    public PersonalData(String firstName, String lastName, String telegramUsername) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.telegramUsername = telegramUsername;
        this.startDate = LocalDateTime.now();
    }

    public String getAdvcashcom() {
        return advcashcom;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public void setAdvcashcom(String advcashcom) {
        this.advcashcom = advcashcom;
    }

    @Override
    public String toString() {
        return "\nПерсональные данные: id="+id+", имя="+firstName+", фамилия="+lastName+", @telegram="+telegramUsername;
    }
}
