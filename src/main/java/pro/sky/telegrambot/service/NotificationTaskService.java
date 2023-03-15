package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationTaskService {
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Transactional
    public void addNotification(Long userId, String message, LocalDateTime localDateTime) {
        NotificationTask notification = new NotificationTask();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setLocalDateTime(localDateTime);
        notificationTaskRepository.save(notification);
    }

    public List check() {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return List.copyOf(notificationTaskRepository.findByLocalDateTime(dateTime));
    }

}
