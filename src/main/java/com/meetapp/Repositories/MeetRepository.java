package com.meetapp.Repositories;

import com.meetapp.Model.MeetEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetRepository extends JpaRepository<MeetEntity, Long> {
    @Query("SELECT m FROM MeetEntity m WHERE m.start_at > CURRENT_TIMESTAMP")
    List<MeetEntity> getAllMeets();

    List<MeetEntity> findAllByUser_Id(Long userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE meetings SET starts_at = :startsAt , end_at = :endAt WHERE id = :meetId", nativeQuery = true)
    void changeMeetDate(@Param("meetId") long meetId,
                        @Param("startsAt") LocalDateTime startsAt,
                        @Param("endAt") LocalDateTime endAt
    );

    @Query("SELECT m FROM MeetEntity m WHERE m.user.id <> :userId")
    List<MeetEntity> getAllWorkersMeets(@Param("userId")long userId);

    @Query("SELECT CASE WHEN (m.user.role = 'ADMIN') THEN true ELSE false END " +
            "FROM MeetEntity m WHERE m.id = :meetingId")
    boolean isMeetingCreatedByAdmin(@Param("meetingId") long meetingId);

}
