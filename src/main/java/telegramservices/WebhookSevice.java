package telegramservices;

import dbservices.ActionType;
import dbservices.DbService;
import dbservices.NoUserInDbException;
import dbservices.UserNotAddInDbException;
import entyties.PersonalData;
import entyties.Service;
import entyties.User;
import main.Config;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import telegramservices.enums.KeyboardCommand;
import telegramservices.enums.ReferalProgCommand;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebhookSevice extends TelegramWebhookBot {
    private static final Logger log = Logger.getLogger(WebhookSevice.class);
    private MenuCreator menucreator;
    private DbService dbService;


    public WebhookSevice() {
        this.menucreator = new MenuCreator();
        this.dbService = DbService.getInstance();
    }


    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        BotApiMethod response=null;
        if (update.hasCallbackQuery()){
            response = callBackContext(update.getCallbackQuery());
        }else if (update.hasMessage()
                &&!update.getMessage().isGroupMessage()
                &&update.getMessage().hasText()) {
            User user = DbService.getInstance().findUser(update.getMessage().getChatId());
            if (user!=null){
                response=mainContext(user,update.getMessage());
            }else {
                response = startContext(update.getMessage());
            }
        }
        return response;
    }

    private BotApiMethod callBackContext(CallbackQuery request) {
        EditMessageText response = new EditMessageText()
                .setMessageId(request.getMessage().getMessageId())
                .setChatId(request.getMessage().getChatId());
        User user = dbService.findUser(request.getMessage().getChatId());
        String dataFromRequest = request.getData();
        ReferalProgCommand referalProgCommand = ReferalProgCommand.getTYPE(dataFromRequest);
        if (referalProgCommand!=ReferalProgCommand.FAIL){
            switch (referalProgCommand){
                case VIEWREFERALS:
                    int parentLevel = user.getLevel();
                    int parentLeftKey = user.getLeftKey();
                    int parentRightKey = user.getRightKey();
                    String text;
                    if (parentLeftKey + 1 == parentRightKey) {
                        text = "У вас нет рефералов";
                    } else {
                        List<User> userList = dbService.getChildrenUsers(parentLevel, parentLeftKey, parentRightKey);
                        StringBuilder level1o = new StringBuilder();
                        StringBuilder level1n = new StringBuilder();
                        int countlevel1=0;
                        StringBuilder level2o = new StringBuilder();
                        StringBuilder level2n = new StringBuilder();
                        StringBuilder level3o = new StringBuilder();
                        StringBuilder level3n = new StringBuilder();
                        for (User u : userList) {
                            if (parentLevel + 1 == u.getLevel()) {
                                if (u.getAdvcashTransactions().size()>0){
                                    level1o.append(u.getUserName()+"+\n");
                                    countlevel1++;
                                }else
                                {
                                    level1n.append(u.getUserName()+"\n");
                                }
                            } else if (parentLevel + 2 == u.getLevel()) {
                                if (u.getAdvcashTransactions().size()>0){
                                    level2o.append(u.getUserName()+"+\n");
                                    countlevel1++;
                                }else
                                {
                                    level2n.append(u.getUserName()+"\n");
                                }
                            } else {
                                if (u.getAdvcashTransactions().size()>0){
                                    level3o.append(u.getUserName()+"+\n");
                                    countlevel1++;
                                }else
                                {
                                    level3n.append(u.getUserName()+"\n");
                                }
                            }
                        }
                        text = "*Рефералы 1го уровня:* "
                                + "\n_(оплативших подписку - "+user.getPersonalData().getReferalsForPrize().size()+")_"
                                + "\n" + level1o.toString()
                                + "\n" + level1n.toString()
                                + "\n*Рефералы 2го уровня:* "
                                + "\n" + level2o.toString()
                                +"\n" + level2n.toString()
                                + "\n*Рефералы 3го уровня:* "
                                + "\n" + level3o.toString()
                                + "\n"+ level3n.toString();
                    }
                    message.setText(text).enableMarkdown(false);
                    break;
                    break;
                case WALLET:
                    response.setText("На вашем счету "+user.getBonus().getCash()+" бонусов" +
                            "\nНомер вашего кошелька advcash"+user.getPersonalData().getAdvcashcom()+
                            "\nПрежде чем создать заявку, убедитесь, что номер вашего кошелька advcash верный. Чтобы сменить его введите команду _/advcash НомерКошелька_ " +
                            "\nЗаявки на выплату обрабатываются в конце недели");
                    response.setReplyMarkup(menucreator.createBonusMenu());
                    break;
                case BACKINREFMENU:
                    String inviteLink= "Чтобы пригласить партнера отправьте ему эту ссылку: https://t.me/tradebeeperbot?start="+user.getChatId();
                    String walletStatus="\nВ вашем кошельке "+user.getBonus().getCash()+" бонусов";
                    String referals = "";
                    if (user.getRightKey()==user.getLeftKey()+1)
                        referals="\nУ вас нет рефералов";
                    else
                        referals="\nКоличесвто ваших рефералов = "+dbService.getChildrenUsers(user.getLevel(),user.getLeftKey(),user.getRightKey()).size();

                    response.setText(inviteLink+
                            walletStatus+
                            referals);
                    response.setReplyMarkup(menucreator.createReferalProgMenu());
                    break;
            }
        }
        return response;
    }


    private BotApiMethod mainContext(User user, Message request) {
        KeyboardCommand command = KeyboardCommand.getTYPE(request.getText());
        SendMessage response = new SendMessage(user.getChatId(),"Неизвестная команда!");
        switch (command){
            case MAINMENU:
                response.setText("Главное меню:");
                response.setReplyMarkup(menucreator.createMainMenu());
                break;
            case SIGNALS:
                if (subscribeIsActive(user.getService()))
                    response.setText("Здесь будут сигналы");
                else
                    response.setText("Сигналы доступны при наличии подписки, вам необходимо ее оплатить.");
                break;
            case NEWS:
                response.setText("Здесь будут новости");
                break;
            case CHAT:
                response.setText("Здесь будет ссылка на чат");
                break;
            case SUBSCRIBE:
                String endSubscribe = "";
                if (user.getService()!=null&&user.getService().getEndOfSubscription()!=null) {
                    if (user.getService().getEndOfSubscription().isBefore(LocalDateTime.now()))
                        endSubscribe = "Ваша подписка заканчивается " + user.getService().getEndOfSubscription().toLocalDate() + ", вы можете её продлить";
                    else
                        endSubscribe="Ваша подписка закончилась " + user.getService().getEndOfSubscription().toLocalDate() + ", вы можете её продлить";
                }
                response.setText(endSubscribe+
                        "\nНа данный момент мы принимаем платежи только с кошельков <a href=\"https://advcash.com/\">AdvCash</a>."+
                        "\nВыберите вариант подписки:\n");
                response.setReplyMarkup(menucreator.createSubscriptionMenu());
                response.enableHtml(true);
                break;
            case REFERALPROG:
                String inviteLink= "Чтобы пригласить партнера отправьте ему эту <a href=\"https://t.me/tradebeeperbot?start="+user.getChatId()+"\">ССЫЛКУ</a>.\n";
                String walletStatus="\nВ вашем кошельке "+user.getBonus().getCash()+" бонусов";
                String referals = "";
                if (user.getRightKey()==user.getLeftKey()+1)
                    referals="\nУ вас нет рефералов";
                else
                    referals="\nКоличесвто ваших рефералов = "+dbService.getChildrenUsers(user.getLevel(),user.getLeftKey(),user.getRightKey()).size();

                response.setText(inviteLink+
                        walletStatus+
                        referals);
                response.setReplyMarkup(menucreator.createReferalProgMenu());
                break;
        }
        return response;
    }

    private boolean subscribeIsActive(Service service) {
        boolean check =false;
        if (service!=null&&
                service.getEndOfSubscription()!=null&&
                service.getEndOfSubscription().isBefore(LocalDateTime.now()))
            check=true;
        return false;
    }

    private BotApiMethod startContext(Message request) {
        SendMessage response = new SendMessage(request.getChatId(),"Ошибка:(, вас нет в базе отправьте /start");
        String textFormRequest = request.getText();
        if (textFormRequest.equals("/start")){
            User user = createNewUser(request, null);
            try {
                dbService.addUser(user, ActionType.ADDROOTUSER);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Ошибка при добавлении в базу " +user);
            }
            response.setText("Добро пожаловать! \nВыберите пункт меню:");
            response.setReplyMarkup(menucreator.createMainMenu());
        }else if (textFormRequest.startsWith("/start")){
            try {
                Integer parentUserId = getParentUserId(request.getText());
                dbService.addUser(createNewUser(request, parentUserId), ActionType.ADDCHILDRENUSER);
                response.setText("Добро пожаловать! \nВыберите пункт меню:");
                response.setReplyMarkup(menucreator.createMainMenu());
            }catch (Exception e) {
                log.error("Юзер с id="+request.getChatId()+" по сслыке с некорректным id пригласителя "+textFormRequest);
                response.setText("Добро пожаловать!"+
                        "\nВ ссылке по которой вы пререшли ошибка - некорректный id пригласителя, сообщите пожалуйста вашему пригласителю и админнистраторам!" +
                        "\nВы можете зарегистрироваться в боте без пригласителя отпрваив /start");
                response.setReplyMarkup(menucreator.createMainMenu());
            }
        }
        return response;
    }

    private Integer getParentUserId(String requestText) throws NoUserInDbException {
        Integer number = null;
        try{
            number = Integer.parseInt(requestText.substring(10));
        }catch (Exception e){
            number=null;
            throw new NoUserInDbException();
        }
        return number;
    }

    public User createNewUser(Message incomingMessage, Integer parentUserId) {
        String firstName = getNameIfIsValid(incomingMessage.getChat().getFirstName());
        String lastName = getNameIfIsValid(incomingMessage.getChat().getLastName());
        long chatId = incomingMessage.getChatId();
        String telegramUserName = "@"+incomingMessage.getChat().getUserName();
        PersonalData personalData = new PersonalData(firstName,lastName,telegramUserName);
        User user = new User(chatId,personalData);
        if (parentUserId!=null) {
            user.setParentId(parentUserId);
            log.info("Создан новый children пользователь:" +user + user.getPersonalData());
        }
        else {
            log.info("Создан новый root пользователь:" + user + user.getPersonalData());
        }
        return user;
    }

    private String createTextForRefMenu(User user){
        String response;
        String inviteLink= "Чтобы пригласить партнера отправьте ему эту ссылку: https://t.me/tradebeeperbot?start="+user.getChatId();
        String walletStatus="\nВ вашем кошельке "+user.getBonus().getCash()+" бонусов";
        String referals = "";
        if (user.getRightKey()==user.getLeftKey()+1)
            referals="\nУ вас нет рефералов";
        else
            referals="\nКоличесвто ваших рефералов = "+dbService.getChildrenUsers(user.getLevel(),user.getLeftKey(),user.getRightKey()).size()+
                    "\nИз них оплатили ";

        response = inviteLink+ walletStatus+ referals;
        return response;
    }

    public String getNameIfIsValid(String name) {
        String validName="-";
        String p = "([\\w]|.)*";
        Pattern pattern = Pattern.compile(p,Pattern.UNICODE_CHARACTER_CLASS);
        if (name!=null) {
            try {
                Matcher matcher = pattern.matcher(name);
                if (matcher.matches()) {
                    validName = name;
                }
            } catch (Exception e) {
                log.info("Не прошло проверку имя "+name);
            }
        }
        return name;
    }

    @Override
    public String getBotUsername() {
        return Config.botname;
    }

    @Override
    public String getBotToken() {
        return Config.token;
    }

    @Override
    public String getBotPath() {
        return Config.botPath;
    }
}
