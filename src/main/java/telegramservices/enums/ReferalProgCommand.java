package telegramservices.enums;

public enum ReferalProgCommand {
    VIEWREFERALS("Посмотреть рефералов"),
    WALLET("Выплатить бонусы"),
    BACKINREFMENU("<=Назад"),
    CREATETASK("Создать заявку на выплату"),


    FAIL("Неизвестная команда");

    ReferalProgCommand(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }

    public static ReferalProgCommand getTYPE(String s){
        ReferalProgCommand type = FAIL;
        for (ReferalProgCommand tempTYPE : ReferalProgCommand.values()){
            if (s.equals(tempTYPE.getText()))
                type = tempTYPE;
        }
        return type;
    }
}
