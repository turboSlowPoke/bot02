package main;

import dbservices.DbService;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegramservices.WebhookSevice;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        DbService.getInstance();
        System.out.println("********dbService started*******");
        ApiContextInitializer.init();
        TelegramWebhookBot webhookService = new WebhookSevice();

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(
                    Config.pathToCertificateStore,
                    Config.certificateStorePassword,
                    Config.EXTERNALWEBHOOKURL,
                    Config.INTERNALWEBHOOKURL,
                    Config.pathToCertificatePublicKey);
        telegramBotsApi.registerBot(webhookService);
        System.out.println("****Telegram Bot started*******");
        log.info("*****Bot started!!******");

        Server server = new Server(80);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath("/");
        ServletHolder staticHolder = new ServletHolder(new DefaultServlet());
        staticHolder.setInitParameter("resourceBase", "./tradebeepercontent/static/");
        staticHolder.setInitParameter("pathInfoOnly", "true");
        contextHandler.addServlet(staticHolder, "/static/*");


        server.setHandler(contextHandler);
        server.start();
        log.info("*******Web Server started*********");
        System.out.println("*******Web Server started*********");
        server.join();


    }
}
