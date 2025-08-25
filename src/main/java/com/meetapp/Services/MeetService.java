package com.meetapp.Services;

import com.meetapp.Model.MeetEntity;
import com.meetapp.Model.UserEntity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public interface MeetService {
    List<MeetEntity> getMeetForEachWorker(String fullName);

    List<MeetEntity> getAllMeetings();

    MeetEntity createMeet(String title , LocalDateTime startsAt ,  LocalDateTime endsAt , String customerName , String createdBy);

    void deleteMeet(String meetId);

    void changeMeetDate(String id, LocalDateTime startAt , LocalDateTime endAt);
}
