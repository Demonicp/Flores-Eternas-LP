package flores.eternas.backend.repository;

import flores.eternas.backend.model.ColorFlor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorFlorRepository extends JpaRepository<ColorFlor, Long> {
}
