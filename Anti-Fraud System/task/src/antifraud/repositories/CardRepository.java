package antifraud.repositories;

import antifraud.models.Card;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends CrudRepository<Card, Long> {
    boolean existsByNumber(@Param("number") String number);
}
