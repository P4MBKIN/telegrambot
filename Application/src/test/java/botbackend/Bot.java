package botbackend;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import javafx.beans.binding.ListBinding;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.*;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.games.CallbackGame;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiValidationException;
import org.telegram.telegrambots.generics.LongPollingBot;
import org.telegram.telegrambots.logging.BotLogger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.*;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

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
                    BufferedImage img = news.get(j).getAllNewsPicture();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(img, "jpg", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());

                    SendPhoto photo = new SendPhoto();
                    photo.setChatId(ids.get(i).longValue());
                    photo.setNewPhoto("newnews", is);
                    photo.setReplyMarkup(createKeyboard(news.get(j).getLinkPost()));
                    //photo.setCaption(news.get(j).getText());
                    System.out.println(news.get(j).getLinks() + "link");
                    //photo.setCaption(news.get(j).getLinks());
                    sendPhoto(photo);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

    }

    InlineKeyboardMarkup createKeyboard(String link){
        InlineKeyboardMarkup mark = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton buta = new InlineKeyboardButton();
        buta.setText("Ссылочка");
        buta.setUrl(link);
        buttons.add(buta);
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(buttons);
        mark.setKeyboard(list);
        return mark;
    }

    public Ability hello() {
        return Ability.builder()
                .name("buy") // Name and command (/hello)
                .info("Says hello world!") // Necessary if you want it to be reported via /commands
                .privacy(PUBLIC)  // Choose from Privacy Class (Public, Admin, Creator)
                .locality(ALL) // Choose from Locality enum Class (User, Group, PUBLIC)
                .input(0) // Arguments required for command (0 for ignore)
                .action(ctx -> {
                    System.out.println("ABILITY");
                    try {
                        System.out.println("wtfwtf");
                        sender.execute(createMess(ctx.chatId(), "Привет в ответ"));
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                })
                .build();
    }

    SendMessage createMess(Long chatid, String text){
        SendMessage a = new SendMessage()
                .setChatId(chatid)
                .setText(text);
        try {
            execute(a);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return a;
    }

    public Bot(){
        super("531412915:AAGIMCbWWt7kL-AtOTf5IuA4IWOFjrKdnWM", "testbot");
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasCallbackQuery()){
            System.out.println("it has callbackquery");
            SendMessage mess = new SendMessage()
                    .setChatId(update.getCallbackQuery().getMessage().getChatId())
                    .setText("Пошел нахуй Алеша" + update.getCallbackQuery().getData());
            try {
                execute(mess);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        else
        {
            SendMessage message = new SendMessage();
            InlineKeyboardMarkup mark = new InlineKeyboardMarkup();

            List<InlineKeyboardButton> buttons = new ArrayList<>();
            InlineKeyboardButton buta = new InlineKeyboardButton();
            buta.setText("Да");
            buta.setCallbackData("yes");
            buttons.add(buta);
            InlineKeyboardButton buta1 = new InlineKeyboardButton();
            buta1.setText("Возможно");
            buta1.setCallbackData("maybe");
            buttons.add(buta1);
            InlineKeyboardButton buta2 = new InlineKeyboardButton();
            buta2.setText("Нет");
            buta2.setCallbackData("no");

            buta2.setCallbackGame(new CallbackGame());
            buttons.add(buta2);
            //but.add(buta);

            List<List<InlineKeyboardButton>> list = new ArrayList<>();
            list.add(buttons);
            mark.setKeyboard(list);
            // Add it to the message
            message.setReplyMarkup(mark);
            message.setText("Нравится ли вам спорт?");
            message.setChatId(update.getMessage().getChatId());
            try{
                execute(message);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            System.out.println();
        }
        //System.out.println(update.getMessage().getText());
        //sayHelloWorld(update);
        /*try {

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
}