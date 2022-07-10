package app.repository;

import app.model.QueryLemma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryLemmaRepository extends JpaRepository<QueryLemma, Integer> {

    @Override
    @Modifying
    @Query("DELETE FROM QueryLemma")
    void deleteAll();
}
