package com.meetapp.Services;

import com.meetapp.Model.MeetEntity;
import com.meetapp.Model.UserEntity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public interface MeetService {
    List<MeetEntity> getMeetForEachWorker(long userId);

    List<MeetEntity> getAllMeetings();

    MeetEntity createMeet(String title , LocalDateTime startsAt ,  LocalDateTime endsAt , String customerName , String createdBy);

    void deleteMeet(Long meetId);

    void changeMeetDate(Long meetId, LocalDateTime startAt , LocalDateTime endAt);
}
