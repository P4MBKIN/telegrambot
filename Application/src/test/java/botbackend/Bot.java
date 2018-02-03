package botbackend;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.LongPollingBot;
import org.telegram.telegrambots.logging.BotLogger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class Bot extends AbilityBot{

    @Override
    public int creatorId(){
        return 123213;
    }

    public void UpdateNews(ArrayList<News> news) throws Exception{
        System.out.println("Inmethod");
        List<Integer> ids = DataBase.getAllChatId();
        System.out.println(ids.size());
        System.out.println("newsize" + news.size());
        for(int i = 0; i < ids.size(); i++)
        {
            for(int j = 0; j < news.size(); j++)
            {
                System.out.println(news.size() + "allo");
                try {
                    BufferedImage img = news.get(j).getImage();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(img, "jpg", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());

                    SendPhoto photo = new SendPhoto();
                    photo.setChatId(ids.get(i).longValue());
                    photo.setNewPhoto("newnews", is);
                    photo.setCaption(news.get(j).getText());
                    sendPhoto(photo);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

    }

    public Bot(){
        super("531412915:AAGIMCbWWt7kL-AtOTf5IuA4IWOFjrKdnWM", "testbot");
    }

    @Override
    public String getBotUsername() {
        return "AntiHeapBot";
        //возвращаем юзера
    }

    public void sayHelloWorld(Update update) {
        if (!update.hasMessage() || !update.getMessage().isUserMessage() || !update.getMessage().hasText() || update.getMessage().getText().isEmpty())
            return;
        User maybeAdmin = update.getMessage().getFrom();
       /* Query DB for if the user is an admin, can be SQL, Reddis, Ignite, etc...
          If user is not an admin, then return here.
       */

        SendMessage snd = new SendMessage();
        snd.setChatId(update.getMessage().getChatId());

        try {
            execute(snd);
        } catch (TelegramApiException e) {
            BotLogger.error("Could not send message", "waat", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        //System.out.println(update.getMessage().getText());
        //sayHelloWorld(update);
        try {
            System.out.println(update.getMessage().getText());
            DataBase.WriteDB(update);
            DataBase.ReadDB();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        /*
        Message msg = e.getMessage(); // Это нам понадобится
        String txt = msg.getText();
        if (txt.equals("/start")) {
            sendMsg(msg, "Добрый день, я бот который будет присылать вам новости! Для начала пройдите небольшой опрос. По шкале от 1 до 4 в порядке возрастания:");


            SendMessage message = new SendMessage();
            message.setChatId(e.getMessage().getChatId());
            message.setText("Насколько вам нравится активный вид отдыха?");

            // Create ReplyKeyboardMarkup object
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            // Create the keyboard (list of keyboard rows)
            List<KeyboardRow> keyboard = new ArrayList<>();
            // Create a keyboard row
            KeyboardRow row = new KeyboardRow();
            // Set each button, you can also use KeyboardButton objects if you need something else than text
            row.add("1");
            row.add("2");
            row.add("3");
            row.add("4");
            // Add the first row to the keyboard
            keyboard.add(row);
            // Create another keyboard row
            // Set the keyboard to the markup
            keyboardMarkup.setKeyboard(keyboard);
            // Add it to the message
            message.setReplyMarkup(keyboardMarkup);

            try {
                // Send the message
                execute(message);
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText(update.getMessage().getText());
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public String getBotToken() {
        return "531412915:AAGIMCbWWt7kL-AtOTf5IuA4IWOFjrKdnWM";
        //Токен бота
    }

    @SuppressWarnings("deprecation") // Означает то, что в новых версиях метод уберут или заменят
    private void sendMsg(Message msg, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId()); // Боту может писать не один человек, и поэтому чтобы отправить сообщение, грубо говоря нужно узнать куда его отправлять
        s.setText(text);
        try { //Чтобы не крашнулась программа при вылете Exception
            sendMessage(s);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

}