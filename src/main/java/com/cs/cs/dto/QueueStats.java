package com.cs.cs.dto;

package com.bank.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueStats {
    private String serviceName;
    private Integer waitingCount;
    private Integer servingCount;
    private Integer completedToday;
    private Double averageWaitTimeMinutes;
}