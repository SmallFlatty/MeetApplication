package com.meetapp.Services;

import com.meetapp.Model.MeetEntity;
import com.meetapp.Repositories.MeetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeetServiceJPA implements MeetService {
    private final MeetRepository meetRepository;

    public MeetServiceJPA(MeetRepository meetRepository) {
        this.meetRepository = meetRepository;
    }

    @Override
    public MeetEntity createMeet(String title, LocalDateTime startsAt, LocalDateTime endsAt, String customerName, String createdBy) {
        MeetEntity meetEntity = new MeetEntity();
        meetEntity.setTitle(title);
        meetEntity.setStart_at(startsAt);
        meetEntity.setEnd_at(endsAt);
        meetEntity.setCustomer_name(customerName);
        meetEntity.setCreated_by(createdBy);

        return  meetRepository.save(meetEntity);
    }


    @Override
    public List<MeetEntity> getMeetForEachWorker(String fullName) {
        return meetRepository.getAllMeetsForEachWorker(fullName);
    }

    @Override
    public List<MeetEntity> getAllMeetings() {
        return meetRepository.getAllMeets();
    }

    @Override
    public void deleteMeet(String meetId) {

    }

    @Override
    public void changeMeetDate(String id, LocalDateTime startAt, LocalDateTime endAt) {

    }
}
