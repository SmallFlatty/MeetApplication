package com.meetapp.Repositories;

import com.meetapp.Model.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    @Query("SELECT u FROM ChatMessageEntity u")
    List<ChatMessageEntity> getAllMessages();
}
