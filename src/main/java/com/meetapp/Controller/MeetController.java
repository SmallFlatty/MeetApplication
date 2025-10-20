package com.meetapp.Controller;

import com.meetapp.Model.MeetEntity;
import com.meetapp.Services.MeetService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/meets")
@CrossOrigin(origins = "*")
public class MeetController {

    private final MeetService meetService;

    public MeetController(MeetService meetService) {
        this.meetService = meetService;
    }

    @PostMapping("/create")
    public MeetEntity createMeet(@RequestBody MeetEntity meetEntity) {
        return meetService.createMeet(
                meetEntity.getTitle(),
                meetEntity.getStartAt(),
                meetEntity.getEndAt(),
                meetEntity.getCustomerName(),
                meetEntity.getCreatedBy(),
                meetEntity.getCreatedAt(),
                meetEntity.getUser().getId()
        );
    }


    @GetMapping("/all-meets")
    public List<MeetEntity> getAllMeets() {
        return meetService.getAllMeetings();
    }

    @GetMapping("/{userId}")
    public List<MeetEntity> getMeetsForPerson(@PathVariable long userId) {
        return meetService.getMeetsForPerson(userId);
    }

    @DeleteMapping("/delete-meet")
    public void deleteMeet(@RequestBody Long meetId) {
        meetService.deleteMeet(meetId);
    }

    @PutMapping("/change-date")
    public void changeMeetDate(@RequestParam Long meetId, @RequestParam LocalDateTime startAt , @RequestParam LocalDateTime endAt ) {
        meetService.changeMeetDate(meetId, startAt, endAt);
    }

    @GetMapping("/get-workers-meets")
    public List<MeetEntity> getAllWorkersMeets(long userId) {
        return meetService.getAllWorkersMeets(userId);
    }

    @GetMapping("/meet-created-admin")
    public boolean meetsCreatedByAdmin(long meetId) {
        return meetService.meetsCreatedByAdmin(meetId);
    }
}
