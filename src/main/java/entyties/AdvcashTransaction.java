package entyties;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ac_transactions")
public class AdvcashTransaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String srcWallet; //Номер кошелькаAdvanced Cash Покупателя.
    private String destWallet;//Номер кошелька Advanced Cash Продавца.
    private BigDecimal amount;//Сумма, начисленная на кошелек Продавца.
    private String merchantCurrency;//Валюта суммы,начисленной на кошелек Продавца.
    private String transferId;//Уникальный ID-номер операции Advanced Cash.
    private LocalDateTime startDate;//давта операции
    private String orderId;//мой идентифицирующий номерпокупки
    private String transactionStatus;
    private String buyerEmail;
    private String hash;//HASH-строка, Проверка данных сервера SCI;
    @OneToOne
    private User user;

     public AdvcashTransaction() {
    }

    public AdvcashTransaction(String srcWallet,
                              String destWallet,
                              BigDecimal amount,
                              String merchantCurrency,
                              String transferId,
                              LocalDateTime startDate,
                              String orderId,
                              String transactionStatus,
                              String buyerEmail,
                              String hash,
                              User user) {
        this.srcWallet = srcWallet;
        this.destWallet = destWallet;
        this.amount = amount;
        this.merchantCurrency = merchantCurrency;
        this.transferId = transferId;
        this.startDate = startDate;
        this.orderId = orderId;
        this.transactionStatus = transactionStatus;
        this.buyerEmail = buyerEmail;
        this.hash = hash;
        this.user = user;
    }

    @Override
    public String toString() {
        String s = "Id: "+id+"\n"
                +"кошелёк покупателя: "+srcWallet+"\n"
                +"кошелёк продавца: "+destWallet+"\n"
                +"начислено продавцу: "+amount+"\n"
                +"Вылюта начисления: "+merchantCurrency+"\n"
                +"id операции: "+transferId+"\n"
                +"дата: "+startDate+"\n"
                +"id покупки: "+orderId+"\n"
                +"статус транзакции: "+transactionStatus+"\n"
                +"email покупателя: "+ buyerEmail +"\n"
                +"ac hash: "+hash;
        return s;
    }
}
