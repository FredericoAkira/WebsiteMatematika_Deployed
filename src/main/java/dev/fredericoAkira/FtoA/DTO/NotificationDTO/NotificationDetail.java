package dev.fredericoAkira.FtoA.DTO.NotificationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDetail {
    private String notifId;
    private String header;
    private String content;
    private String senderName;
    private String status;
}
