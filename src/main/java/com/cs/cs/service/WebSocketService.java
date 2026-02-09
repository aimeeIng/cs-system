package com.cs.cs.service;

package com.bank.queue.service;

import com.bank.queue.dto.DisplayUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastDisplayUpdate(DisplayUpdate update) {
        log.info("Broadcasting display update: {}", update);
        messagingTemplate.convertAndSend("/topic/display", update);
    }

    public void broadcastQueueUpdate(String serviceName, Integer waitingCount) {
        messagingTemplate.convertAndSend("/topic/queue/" + serviceName, waitingCount);
    }
}