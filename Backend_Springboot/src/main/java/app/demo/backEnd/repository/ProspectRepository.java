package app.demo.backEnd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import app.demo.backEnd.model.entity.Prospect;
import jakarta.transaction.Transactional;

public interface ProspectRepository extends JpaRepository<Prospect, Long> {
    List<Prospect> findByIsSMImportedFalse();

    @Modifying
    @Transactional
    @Query("""
                UPDATE Prospect p
                SET p.isSMImported = true
                WHERE p.id IN :ids
            """)
    void markAsImported(@Param("ids") List<Long> ids);
}
