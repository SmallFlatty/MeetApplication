package com.meetapp.Services;

import com.meetapp.Model.MeetEntity;
import com.meetapp.Model.UserEntity;
import com.meetapp.Repositories.MeetRepository;
import com.meetapp.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeetServiceJPA implements MeetService {
    private final MeetRepository meetRepository;
    private final UserRepository userRepository;

    public MeetServiceJPA(MeetRepository meetRepository, UserRepository userRepository) {
        this.meetRepository = meetRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MeetEntity createMeet(String title, LocalDateTime startsAt, LocalDateTime endsAt, String customerName, String createdBy,LocalDateTime createdAt,long userId) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MeetEntity meetEntity = new MeetEntity();
        meetEntity.setTitle(title);
        meetEntity.setStartAt(startsAt);
        meetEntity.setEndAt(endsAt);
        meetEntity.setCustomerName(customerName);
        meetEntity.setCreatedBy(createdBy);
        meetEntity.setCreatedAt(createdAt);
        meetEntity.setUser(user);

        return  meetRepository.save(meetEntity);
    }

    @Override
    public List<MeetEntity> getMeetsForPerson(long userId) {
        return meetRepository.findAllByUser_Id(userId);
    }

    @Override
    public List<MeetEntity> getAllMeetings() {
        return meetRepository.getAllMeets();
    }

    @Override
    public List<MeetEntity> getAllWorkersMeets(long userId) {
        return meetRepository.getAllWorkersMeets(userId);
    }

    @Override
    public void deleteMeet(Long meetId) {
        meetRepository.deleteById(meetId);
    }

    @Override
    public void changeMeetDate(Long meetId, LocalDateTime startAt, LocalDateTime endAt) {
        if(endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        meetRepository.changeMeetDate(meetId, startAt, endAt);
    }

    @Override
    public boolean meetsCreatedByAdmin(long meetId) {
        return meetRepository.isMeetingCreatedByAdmin(meetId);
    }
}
