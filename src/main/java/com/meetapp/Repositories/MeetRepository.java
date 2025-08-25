package com.meetapp.Repositories;

import com.meetapp.Model.MeetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetRepository extends JpaRepository<MeetEntity, Long> {
    @Query(value = "SELECT * FROM meetings m WHERE m.start_at > NOW()", nativeQuery = true)
    List<MeetEntity> getAllMeets();

    @Query(value = "SELECT * FROM meetings WHERE created_by = :fullName", nativeQuery = true)
    List<MeetEntity> getAllMeetsForEachWorker(@Param("fullName") String fullName);

}
