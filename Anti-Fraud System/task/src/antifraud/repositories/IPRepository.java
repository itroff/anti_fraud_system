package antifraud.repositories;

import antifraud.models.IP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface IPRepository extends CrudRepository<IP, Long> {
    boolean existsByIp(@Param("ip") String ip);
}
