package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogRepository extends JpaRepository<LogEntity, UUID> {
}
