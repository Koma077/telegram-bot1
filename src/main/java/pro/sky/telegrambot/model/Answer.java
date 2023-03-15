package pro.sky.telegrambot.model;

public enum Answer {
    HELLO("Приветствую пользователь, ты можещь запланировать событие в формате: 01.01.2022 20:00 Сделать домашнюю работу"),
    DONE("Событие успешно добавлено"),
    MISTAKE("Неправльный формат события! Сделайте запрос по примеру: 01.01.2022 20:00 Сделать домашнюю работу"),
    ERROR("Неправльный формат события! Сделайте запрос по примеру: 01.01.2022 20:00 Сделать домашнюю работу"),
    REMINDER("Вы просили напомнить о событии:");

    private String answer;

    Answer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return answer;
    }
}
