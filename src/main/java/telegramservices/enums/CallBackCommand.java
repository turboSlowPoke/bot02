package telegramservices.enums;

public enum CallBackCommand {
    VIEWREFERALS("Посмотреть рефералов"),
    BONUSMENU("Выплатить бонусы"),
    BACKINREFMENU("<=Назад"),
    CREATETASKFORPAYMENT("Создать заявку на выплату"),


    FAIL("Неизвестная команда");

    CallBackCommand(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }

    public static CallBackCommand getTYPE(String s){
        CallBackCommand type = FAIL;
        for (CallBackCommand tempTYPE : CallBackCommand.values()){
            if (s.equals(tempTYPE.getText()))
                type = tempTYPE;
        }
        return type;
    }
}
