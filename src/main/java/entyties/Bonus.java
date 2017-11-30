package entyties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bonuswallets")
public class Bonus {
    @Id @Column(name = "id") @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "cash",scale = 2,precision = 10)
    private BigDecimal cash;
    @ElementCollection  @Column(name = "paidreferals")
    private List<Integer> paidReferalsIdList;

    public Bonus() {
        cash=new BigDecimal("0.0");
    }

    public BigDecimal getCash() {
        return cash;
    }

    public List<Integer> getPaidReferalsIdList() {
        if (paidReferalsIdList==null)
            paidReferalsIdList=new ArrayList<>();
        return paidReferalsIdList;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public void addPaidReferalsId(Integer userId){
        if (paidReferalsIdList==null)
            paidReferalsIdList = new ArrayList<>();
        paidReferalsIdList.add(userId);
    }
}
