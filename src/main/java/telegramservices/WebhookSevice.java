package telegramservices;

import dbservices.ActionType;
import dbservices.DbService;
import dbservices.NoUserInDbException;
import entyties.*;
import main.Config;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import telegramservices.enums.MainCommand;
import telegramservices.enums.CallBackCommand;

import java.math.BigDecimal;
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
        CallBackCommand referalProgCommand = CallBackCommand.getTYPE(dataFromRequest);
        if (referalProgCommand!= CallBackCommand.FAIL){
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
                        StringBuilder level2o = new StringBuilder();
                        StringBuilder level2n = new StringBuilder();
                        StringBuilder level3o = new StringBuilder();
                        StringBuilder level3n = new StringBuilder();
                        for (User u : userList) {
                            if (parentLevel + 1 == u.getLevel()) {
                                if (u.getService()!=null){
                                    level1o.append(u.getPersonalData().getTelegramUsername()+"+\n");
                                }else
                                {
                                    level1n.append(u.getPersonalData().getTelegramUsername()+"\n");
                                }
                            } else if (parentLevel + 2 == u.getLevel()) {
                                if (u.getService()!=null){
                                    level2o.append(u.getPersonalData().getTelegramUsername()+"+\n");
                                }else
                                {
                                    level2n.append(u.getPersonalData().getTelegramUsername()+"\n");
                                }
                            } else {
                                if (u.getService()!=null){
                                    level3o.append(u.getPersonalData().getTelegramUsername()+"+\n");
                                }else
                                {
                                    level3n.append(u.getPersonalData().getTelegramUsername()+"\n");
                                }
                            }
                        }
                        text = "<b>Рефералы 1го уровня:</b> "
                                + "\n<i>(оплативших подписку - "+user.getBonus().getPaidReferalsIdList().size()+")</i>\n"
                                +  level1o.toString()
                                +  level1n.toString()
                                + "\n<b>Рефералы 2го уровня:</b> "
                                +  level2o.toString()
                                +  level2n.toString()
                                + "\n<b>Рефералы 3го уровня:</b> "
                                +  level3o.toString()
                                +  level3n.toString()+"\n";
                    }
                    response.setText(text).enableHtml(true);
                    response.setReplyMarkup(menucreator.createBackInRefMenu());
                    break;
                case BONUSMENU:
                    response.setText("На вашем счету "+user.getBonus().getCash()+" бонусов" +
                            "\nНомер вашего кошелька advcash: "+user.getPersonalData().getAdvcashcom()+
                            "\nПрежде чем создать заявку, убедитесь, что номер вашего <b>долларового</b> кошелька advcash верный. Чтобы сменить его введите команду: \n<b>/advcash</b> <i>НомерКошелька</i> " +
                            "\nЗаявки на выплату обрабатываются в конце недели");
                    response.setReplyMarkup(menucreator.createBonusMenu());
                    response.enableHtml(true);
                    break;
                case BACKINREFMENU:
                    response.setText(createTextForRefMenu(user));
                    response.setReplyMarkup(menucreator.createReferalProgMenu());
                    response.enableHtml(true);
                    break;
                case CREATETASKFORPAYMENT:
                    //если в кошльке больше 0
                    if (user.getBonus().getCash()!=null&&
                            user.getBonus().getCash().compareTo(new BigDecimal("0.0"))==1){
                        Task task = dbService.findOpenPayBonusesTask(user);
                        //если уже есть заявка
                        if (task!=null){
                            response.setText("Вы уже создали заявку "+task.getDateTimeOpening().toLocalDate()+", id заявки="+task.getId()+
                            "\nЗаявки на выплату обрабатываются в конце недели");
                        }else {
                            task=new Task(TaskType.PAY_BONUSES,user);
                            dbService.mergeEntyti(task);
                            response.setText("Заявка создана");
                        }

                    }else {
                        response.setText("Бонусов не достаточно для выплаты! \nНужно больше бонусов:-)");
                    }
                    response.setReplyMarkup(menucreator.createBackInRefMenu());
                    break;
            }
        }
        return response;
    }

    private BotApiMethod mainContext(User user, Message request) {
        MainCommand command = MainCommand.getTYPE(request.getText());
        SendMessage response = new SendMessage(user.getChatId(),"Неизвестная команда!");
        if (command!= MainCommand.FAIL) {
            switch (command) {
                case START:
                    response.setText("Главное меню:");
                    response.setReplyMarkup(menucreator.createMainMenu());
                    break;
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
                    if (user.getService() != null && user.getService().getEndOfSubscription() != null) {
                        if (user.getService().getEndOfSubscription().isBefore(LocalDateTime.now()))
                            endSubscribe = "Ваша подписка заканчивается " + user.getService().getEndOfSubscription().toLocalDate() + ", вы можете её продлить";
                        else
                            endSubscribe = "Ваша подписка закончилась " + user.getService().getEndOfSubscription().toLocalDate() + ", вы можете её продлить";
                    }
                    response.setText(endSubscribe +
                            "\nНа данный момент мы принимаем платежи только с кошельков <a href=\"https://advcash.com/\">AdvCash</a>." +
                            "\nВыберите вариант подписки:\n");
                    response.setReplyMarkup(menucreator.createSubscriptionMenu());
                    response.enableHtml(true);
                    break;
                case REFERALPROG:
                    response.setText(createTextForRefMenu(user));
                    response.setReplyMarkup(menucreator.createReferalProgMenu());
                    response.enableHtml(true);
                    break;
            }
        }else if (request.getText().startsWith("/advcash")){
            String advcashVallet = getValidAdvcash(request.getText().substring(9));
            if (advcashVallet!=null) {
                PersonalData personalData = user.getPersonalData();
                personalData.setAdvcashcom(advcashVallet);
                dbService.mergeEntyti(personalData);
                log.info("юзер "+user+" обновил свой advcash "+request.getText());
                response.setText("Номер кошелка advcash изменён на "+personalData.getAdvcashcom());
            }else {
                log.info("юзер "+user+"неправильно ввел номер advcash:"+request.getText());
                System.out.println("юзер "+user+"неправильно ввел номер advcash:"+request.getText());
                response.setText("Номер кошелька не корректный, укажите номер долларового кошелька, в одном из форматов" +
                        "\n/advcash U 1111 2222 3333" +
                        "\n/advcash U123412341234" +
                        "\n/advcash 1234 1234 1234" +
                        "\n/advcash 123412341234");
            }
        }
        return response;
    }

    private String getValidAdvcash(String substring) {
        String advcash="";
        String p = "(U(\\s\\d{4}){3})|(U(\\d){12})|((\\d){12})|(\\d{4}(\\s\\d{4}){2})";//"(U[\\s[\\d]{4})]{3})";
        Pattern pattern = Pattern.compile(p,Pattern.UNICODE_CHARACTER_CLASS);
        if (advcash!=null) {
            try {
                Matcher matcher = pattern.matcher(substring);
                if (matcher.matches())
                    advcash = substring;
                else
                    advcash=null;

            } catch (Exception e) {
                System.out.println("Не прошло проверку имя "+substring);
            }
        }
        return advcash;
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
        System.out.println("start контекст: "+request.getText());
        SendMessage response = new SendMessage(request.getChatId(),"Опаньки :( \nОшибка,  вас нет в базе отправьте /start");
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
                Long parentUserId = getParentUserId(request.getText());
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

    private Long getParentUserId(String requestText) throws NoUserInDbException {
        Long number = null;
        try{
            number = Long.parseLong(requestText.substring(7));
        }catch (Exception e){
            number=null;
            throw new NoUserInDbException();
        }
        return number;
    }

    public User createNewUser(Message incomingMessage, Long parentUserId) {
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
        String inviteLink= "Чтобы пригласить партнера отправьте ему эту <a href=\"https://t.me/tradebeeperbot?start="+user.getChatId()+"\">ссылку</a>";
        String walletStatus="\nВ вашем кошельке "+user.getBonus().getCash()+" бонусов";
        String referals = "";
        if (user.getRightKey()==user.getLeftKey()+1)
            referals="\nУ вас нет рефералов";
        else
            referals="\nКоличесвто ваших рефералов "+dbService.getChildrenUsers(user.getLevel(),user.getLeftKey(),user.getRightKey()).size();
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
