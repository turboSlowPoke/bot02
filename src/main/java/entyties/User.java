package entyties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id @Column(name = "id") @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "level") @NotNull
    private int level;
    @Column(name = "leftkey") @NotNull
    private int leftKey;
    @Column(name = "rightkey") @NotNull
    private int rightKey;
    @Column(name = "parentid")
    private long parentId;
    @Column(name = "chatId")
    private long chatId;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PersonalData personalData;
    @Column(name = "type")
    private String type = "customer";
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Service service;
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Bonus bonus;
    @ManyToMany(mappedBy = "users",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<AdvcashTransaction> advcashTransactions;


    public User() {}

    public User(long chatId, PersonalData personalData) {
        this.chatId=chatId;
        this.personalData = personalData;
        this.service=new Service();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setLeftKey(int leftKey) {
        this.leftKey = leftKey;
    }

    public void setRightKey(int rightKey) {
        this.rightKey = rightKey;
    }

    public int getLevel() {
        return level;
    }

    public int getLeftKey() {
        return leftKey;
    }

    public int getRightKey() {
        return rightKey;
    }

    public PersonalData getPersonalData() {
        return personalData;
    }

    public long getChatId() {
        return chatId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getParentId() {
        return parentId;
    }

    public Bonus getBonus() {
        if (bonus==null)
            bonus = new Bonus();
        return bonus;
    }

    public Service getService() {
        return service;
    }

    public List<AdvcashTransaction> getAdvcashTransactions() {
        return advcashTransactions;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "\nПользователь: userID="+id+", chatId="+chatId +", level="+level +", RK="+rightKey +", LK="+leftKey+", parentId="+parentId;
    }
}
