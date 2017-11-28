package telegramservices;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import telegramservices.enums.MainCommand;
import telegramservices.enums.CallBackCommand;

import java.util.ArrayList;
import java.util.List;

public class MenuCreator {
    public ReplyKeyboard createMainMenu() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(MainCommand.SIGNALS.getText()));
        keyboardRow1.add(new KeyboardButton(MainCommand.NEWS.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(MainCommand.SUBSCRIBE.getText()));
        keyboardRow2.add(new KeyboardButton(MainCommand.CHAT.getText()));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton(MainCommand.REFERALPROG.getText()));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }


    public InlineKeyboardMarkup createSubscriptionMenu() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText(MainCommand.SUBSCRIPTION1.getText()).setUrl("advcash.com"));
        rowInline1.add(new InlineKeyboardButton().setText(MainCommand.SUBSCRIPTION2.getText()).setUrl("advcash.com"));
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(new InlineKeyboardButton().setText(MainCommand.SUBSCRIPTION3.getText()).setUrl("advcash.com"));
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup createReferalProgMenu() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText(CallBackCommand.VIEWREFERALS.getText()).setCallbackData(CallBackCommand.VIEWREFERALS.getText()));
        rowInline1.add(new InlineKeyboardButton().setText(CallBackCommand.BONUSMENU.getText()).setCallbackData(CallBackCommand.BONUSMENU.getText()));
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup createBonusMenu(){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText(CallBackCommand.BACKINREFMENU.getText()).setCallbackData(CallBackCommand.BACKINREFMENU.getText()));
        rowInline1.add(new InlineKeyboardButton().setText(CallBackCommand.CREATETASKFORPAYMENT.getText()).setCallbackData(CallBackCommand.CREATETASKFORPAYMENT.getText()));
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup createBackInRefMenu() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText(CallBackCommand.BACKINREFMENU.getText()).setCallbackData(CallBackCommand.BACKINREFMENU.getText()));
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}
