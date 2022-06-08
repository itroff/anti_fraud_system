package antifraud.repositories;

import antifraud.models.TransactionRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends CrudRepository<TransactionRequest, Long> {
    List<TransactionRequest> findByNumber(@Param("number") String number);

}
