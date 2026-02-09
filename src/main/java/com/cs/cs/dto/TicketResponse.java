package com.cs.cs.dto;

package com.bank.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String ticketNumber;
    private String phoneNumber;
    private String serviceName;
    private String status;
    private Integer queuePosition;
    private Integer windowNumber;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedWaitTime;
}
