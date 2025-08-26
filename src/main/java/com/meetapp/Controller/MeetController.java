package com.meetapp.Controller;

import com.meetapp.Model.MeetEntity;
import com.meetapp.Services.MeetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meets")
@CrossOrigin(origins = "*")
public class MeetController {

    private final MeetService meetService;

    public MeetController(MeetService meetService) {
        this.meetService = meetService;
    }

    @PostMapping("/meetings")
    public MeetEntity createMeet(@RequestBody MeetEntity meetEntity) {
        return meetService.createMeet(meetEntity.getTitle(),
                                      meetEntity.getStart_at(),
                                      meetEntity.getEnd_at(),
                                      meetEntity.getCustomer_name(),
                                      meetEntity.getCreated_by());
    }

    @GetMapping("/meetings")
    public List<MeetEntity> getAllMeets() {
        return meetService.getAllMeetings();
    }

//    @GetMapping("/meetings/{fullName}")
//    public List<MeetEntity> getMeetsByFullName(@PathVariable String fullName) {
//        return meetService.getMeetForEachWorker(fullName);
//    } Змінив дб на One to Many , зробити щоб тут можна було брати все по Id
}
