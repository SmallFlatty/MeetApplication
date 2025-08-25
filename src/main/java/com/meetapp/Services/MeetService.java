package com.meetapp.Services;

import com.meetapp.Model.MeetEntity;

public interface MeetService {
    MeetEntity getMeet(String meetId);
    MeetEntity createMeet(MeetEntity meet);

    void deleteMeet(String id);

}
