package main;

import dbservices.DbService;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegramservices.WebhookSevice;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    public static void main(String[] args){
        DbService.getInstance();
        System.out.println("********dbService started*******");
        ApiContextInitializer.init();
        TelegramWebhookBot webhookService = new WebhookSevice();


        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(
                    Config.pathToCertificateStore,
                    Config.certificateStorePassword,
                    Config.EXTERNALWEBHOOKURL,
                    Config.INTERNALWEBHOOKURL,
                    Config.pathToCertificatePublicKey);
            telegramBotsApi.registerBot(webhookService);
            System.out.println("****Telegram Bot started*******");
            log.info("*****Bot started!!******");

        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

    }
}
