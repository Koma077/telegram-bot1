package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Answer;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    @Autowired
    NotificationTaskRepository notificationTaskRepository;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final Pattern pattern = Pattern.compile("([\\d\\\\.:\\s]{16})(\\s)([A-яA-z\\s\\d,.!?:]+)");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    private TelegramBot telegramBot;
    private NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            updates.forEach(update -> {
                Long chatId = update.message().chat().id();
                String user = update.message().text();
                logger.info("Processing update: {}", update);
                if ("/start".equals(user)) {
                    SendMessage message = new SendMessage(chatId, Answer.HELLO.toString());
                    telegramBot.execute(message);
                } else if (user != null) {
                    Matcher matcher = pattern.matcher(update.message().text());
                    LocalDateTime dateTime;
                    if (matcher.matches() && (dateTime = parse(matcher.group(1))) != null) {
                        String messageText = matcher.group(3);
                        telegramBot.execute(new SendMessage(chatId, Answer.DONE.toString()));
                    } else {
                        telegramBot.execute(new SendMessage(chatId, Answer.MISTAKE.toString()));
                    }
                } else {
                    telegramBot.execute(new SendMessage(chatId, Answer.ERROR.toString()));
                }
                sendMessageFromDataBase();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Nullable
    private LocalDateTime parse(String dataTime) {
        try {
            return LocalDateTime.parse(dataTime, formatter);
        } catch (DateTimeException e) {
            return null;
        }
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendMessageFromDataBase() {
        List<NotificationTask> notificationList = notificationTaskService.check();
        notificationList
                .forEach(notificationTask -> {
                    telegramBot.execute(new SendMessage(notificationTask.getUserId(), Answer.REMINDER + notificationTask.getMessage()));
                });
    }
}
