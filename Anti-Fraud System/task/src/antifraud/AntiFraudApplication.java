package antifraud;

import antifraud.models.Limit;
import antifraud.models.Rule;
import antifraud.models.TransactionResult;
import antifraud.repositories.LimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
public class AntiFraudApplication {
    public static void main(String[] args) {
        SpringApplication.run(AntiFraudApplication.class, args);
    }

    @Autowired
    LimitRepository repository;


    @Bean
    public Map<Rule, List<Rule.Action>> rules() {
        return Map.of(new Rule(TransactionResult.ALLOWED, TransactionResult.ALLOWED), List.of(Rule.Action.EXCEPTION),
                new Rule(TransactionResult.MANUAL_PROCESSING, TransactionResult.ALLOWED), List.of(Rule.Action.INCREASE_ALLOWED),
                new Rule(TransactionResult.PROHIBITED, TransactionResult.ALLOWED), List.of(Rule.Action.INCREASE_ALLOWED, Rule.Action.INCREASE_MANUAL),
                new Rule(TransactionResult.ALLOWED, TransactionResult.MANUAL_PROCESSING), List.of(Rule.Action.DECREASE_ALLOWED),
                new Rule(TransactionResult.MANUAL_PROCESSING, TransactionResult.MANUAL_PROCESSING), List.of(Rule.Action.EXCEPTION),
                new Rule(TransactionResult.PROHIBITED, TransactionResult.MANUAL_PROCESSING), List.of(Rule.Action.INCREASE_MANUAL),
                new Rule(TransactionResult.ALLOWED, TransactionResult.PROHIBITED), List.of(Rule.Action.DECREASE_ALLOWED, Rule.Action.DECREASE_MANUAL),
                new Rule(TransactionResult.MANUAL_PROCESSING, TransactionResult.PROHIBITED), List.of(Rule.Action.DECREASE_MANUAL),
                new Rule(TransactionResult.PROHIBITED, TransactionResult.PROHIBITED), List.of(Rule.Action.EXCEPTION));
    }

    @Bean
    public Limit getLimit() {
        Optional<Limit> limit = repository.findById(1L);
        return limit.orElse(new Limit(200L, 1500L));
    }
}