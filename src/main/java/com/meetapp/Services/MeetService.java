package com.meetapp.Services;

import com.meetapp.Model.MeetEntity;
import java.time.LocalDateTime;
import java.util.List;

public interface MeetService {
    List<Object []> getMeetsForPerson(long userId);

    List<MeetEntity> getAllMeetings();

    List<MeetEntity> getAllWorkersMeets(long userId);

    MeetEntity createMeet(String title , LocalDateTime startsAt ,  LocalDateTime endsAt , String customerName , String createdBy ,LocalDateTime createdAt, long userId);

    void deleteMeet(Long meetId);

    void changeMeetDate(Long meetId, LocalDateTime startAt , LocalDateTime endAt);

    boolean meetsCreatedByAdmin(long meetId);

    List<Long> getAllMeetsId();

    List<Long> getIdMeetsForWorker(long userId);
}
