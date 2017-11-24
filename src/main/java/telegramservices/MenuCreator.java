package telegramservices;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import telegramservices.enums.KeyboardCommand;
import telegramservices.enums.ReferalProgCommand;

import java.util.ArrayList;
import java.util.List;

public class MenuCreator {
    public ReplyKeyboard createMainMenu() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(KeyboardCommand.SIGNALS.getText()));
        keyboardRow1.add(new KeyboardButton(KeyboardCommand.NEWS.getText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(KeyboardCommand.SUBSCRIBE.getText()));
        keyboardRow2.add(new KeyboardButton(KeyboardCommand.CHAT.getText()));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton(KeyboardCommand.REFERALPROG.getText()));
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
        rowInline1.add(new InlineKeyboardButton().setText(KeyboardCommand.SUBSCRIPTION1.getText()).setUrl("advcash.com"));
        rowInline1.add(new InlineKeyboardButton().setText(KeyboardCommand.SUBSCRIPTION2.getText()).setUrl("advcash.com"));
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(new InlineKeyboardButton().setText(KeyboardCommand.SUBSCRIPTION3.getText()).setUrl("advcash.com"));
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup createReferalProgMenu() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText(ReferalProgCommand.VIEWREFERALS.getText()).setCallbackData(ReferalProgCommand.VIEWREFERALS.getText()));
        rowInline1.add(new InlineKeyboardButton().setText(ReferalProgCommand.WALLET.getText()).setCallbackData(ReferalProgCommand.WALLET.getText()));
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup createBonusMenu(){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton().setText(ReferalProgCommand.BACKINREFMENU.getText()).setCallbackData(ReferalProgCommand.BACKINREFMENU.getText()));
        rowInline1.add(new InlineKeyboardButton().setText(ReferalProgCommand.CREATETASK.getText()).setCallbackData(ReferalProgCommand.CREATETASK.getText()));
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}
