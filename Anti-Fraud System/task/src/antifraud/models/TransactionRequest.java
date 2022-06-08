package antifraud.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity(name = "transactions")
public class TransactionRequest {

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionId;

    @Column
    private Long amount;

    @Column
    private String ip;

    @Column
    private String number;

    @Column
    @Enumerated(EnumType.STRING)
    private Region region;

    @Column
    private LocalDateTime date;

    public TransactionResult getResult() {
        return result;
    }

    public void setResult(TransactionResult result) {
        this.result = result;
    }

    public TransactionResult getFeedback() {
        return feedback;
    }

    public void setFeedback(TransactionResult feedback) {
        this.feedback = feedback;
    }

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionResult result;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionResult feedback = TransactionResult.UNDEFINED;


    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public static void findAllTransactionByNumberAndDateBetween(List<TransactionRequest> list,
                                                                TransactionRequest request,
                                                                Map<String, TransactionResult> checks) {
        LocalDateTime date = request.getDate();
        LocalDateTime date2 = date.minusHours(1);
        Set<Region> regions = new HashSet<>();
        Set<String> ips = new HashSet<>();
        for (TransactionRequest req : list) {
            if ((req.getDate().isAfter(date2) && req.getDate().isBefore(date))
                    || req.getDate().isEqual(date2) || req.getDate().isEqual(date)) {
                regions.add(req.getRegion());
                ips.add(req.getIp());
            }
        }
        if (regions.size() > 3) {
            checks.put("region-correlation", TransactionResult.PROHIBITED);
        } else if (regions.size() == 3) {
            checks.put("region-correlation", TransactionResult.MANUAL_PROCESSING);
        }

        if (ips.size() > 3) {
            checks.put("ip-correlation", TransactionResult.PROHIBITED);
        } else if (ips.size() == 3) {
            checks.put("ip-correlation", TransactionResult.MANUAL_PROCESSING);
        }
    }

}
