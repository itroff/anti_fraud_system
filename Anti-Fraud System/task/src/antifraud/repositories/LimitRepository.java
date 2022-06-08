package antifraud.repositories;

import antifraud.models.Limit;
import org.springframework.data.repository.CrudRepository;

public interface LimitRepository extends CrudRepository<Limit, Long> {
}
