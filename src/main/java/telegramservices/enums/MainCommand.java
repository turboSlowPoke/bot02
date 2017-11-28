package telegramservices.enums;

public enum MainCommand {
    START("/start"),
    MAINMENU("Главное меню"),
    //mainmenu
    SIGNALS("Сигналы"),
    NEWS("Новости"),
    CHAT("Чат"),
    SUBSCRIBE("Подписка"),
    REFERALPROG("Реферальная программа"),
    //подписка
    SUBSCRIPTION1("Месяц 50$"),
    SUBSCRIPTION2("Три месяца 120$"),
    SUBSCRIPTION3("Шесть месяцев 190$"),

    FAIL("Неизвестная команда");

    MainCommand(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }

    public static MainCommand getTYPE(String s){
        MainCommand type = FAIL;
        for (MainCommand tempTYPE : MainCommand.values()){
            if (s.equals(tempTYPE.getText()))
                type = tempTYPE;
        }
        return type;
    }
}
