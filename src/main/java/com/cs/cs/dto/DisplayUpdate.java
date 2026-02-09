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
public class DisplayUpdate {
    private CurrentTicket current;
    private NextTicket next;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentTicket {
        private String ticketNumber;
        private Integer windowNumber;
        private String serviceName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NextTicket {
        private String ticketNumber;
        private String serviceName;
    }
}