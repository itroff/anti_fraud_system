package antifraud.models;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity(name = "limit2")
@Table(name = "limit2")
public class Limit {

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Id
    @Column
    private long id = 1;
    @Autowired
    @Transient
    private Map<Rule, List<Rule.Action>> rules;

    public Limit() {
    }

    public long getLimitAllowed() {
        return limitAllowed;
    }

    public void setLimitAllowed(long limitAllowed) {
        this.limitAllowed = limitAllowed;
    }

    public long getLimitManual() {
        return limitManual;
    }

    public void setLimitManual(long limitManual) {
        this.limitManual = limitManual;
    }

    @Column
    private long limitAllowed;

    @Column
    private long limitManual;

    public Limit(long limitAllowed, long limitManual) {
        this.limitAllowed = limitAllowed;
        this.limitManual = limitManual;
    }

    public Rule.Action processFeedback(TransactionRequest transaction, TransactionRequest request) {
        Rule rule = new Rule(transaction.getResult(), request.getFeedback());
        long value = transaction.getAmount();
        if (rules.containsKey(rule)) {
            List<Rule.Action> actions = rules.get(rule);
            for (Rule.Action action : actions) {
                if (action == Rule.Action.INCREASE_ALLOWED) {
                    this.limitAllowed = (long) Math.ceil(0.8 * this.limitAllowed + 0.2 * value);
                } else if (action == Rule.Action.INCREASE_MANUAL) {
                    this.limitManual = (long) Math.ceil(0.8 * this.limitManual + 0.2 * value);
                } else if (action == Rule.Action.DECREASE_ALLOWED) {
                    this.limitAllowed = (long) Math.ceil(0.8 * this.limitAllowed - 0.2 * value);
                } else if (action == Rule.Action.DECREASE_MANUAL) {
                    this.limitManual = (long) Math.ceil(0.8 * this.limitManual - 0.2 * value);
                } else if (action == Rule.Action.EXCEPTION) {
                    return action;
                }
            }
        }
        return Rule.Action.INCREASE_ALLOWED;
    }

}
