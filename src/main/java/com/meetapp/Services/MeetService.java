package com.meetapp.Services;

import com.meetapp.Model.MeetEntity;
import java.time.LocalDateTime;
import java.util.List;

public interface MeetService {
    List<MeetEntity> getMeetsForPerson(long userId);

    List<MeetEntity> getAllMeetings();

    MeetEntity createMeet(String title , LocalDateTime startsAt ,  LocalDateTime endsAt , String customerName , String createdBy , long userId);

    void deleteMeet(Long meetId);

    void changeMeetDate(Long meetId, LocalDateTime startAt , LocalDateTime endAt);
}
