package botbackend;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import javafx.beans.binding.ListBinding;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.*;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.api.objects.CallbackQuery;
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
import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.*;
import java.util.function.Consumer;

import static botbackend.VKNames.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

public class Bot extends AbilityBot{

    List<String> Question = new ArrayList<>();


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
                try
                {
                    BufferedImage img = news.get(j).getAllNewsPicture();
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(img, "jpg", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());

                    SendPhoto photo = new SendPhoto();
                    photo.setChatId(ids.get(i).longValue());
                    photo.setNewPhoto("newnews", is);
                    photo.setReplyMarkup(createKeyboard(news.get(j).getLinkPost()));
                    System.out.println(news.get(j).getLinks() + "link");
                    sendPhoto(photo);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }

    }

    public void UpdateNewsToChatId(ArrayList<News> news, Long chatId) {
        for (int j = 0; j < news.size(); j++) {
            System.out.println(news.size() + "allo");
            try {
                BufferedImage img = news.get(j).getAllNewsPicture();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(img, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                SendPhoto photo = new SendPhoto();
                photo.setChatId(chatId);
                photo.setNewPhoto("newnews", is);
                photo.setReplyMarkup(createKeyboard(news.get(j).getLinkPost()));
                System.out.println(news.get(j).getLinks() + "link");
                sendPhoto(photo);
            } catch (Exception ex) {
                ex.printStackTrace();
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

    public Bot(){
        super("531412915:AAGIMCbWWt7kL-AtOTf5IuA4IWOFjrKdnWM", "testbot");
        Question.add("Интересуетесь ли вы политикой?");
        Question.add("Нравится ли вам кино?");
        Question.add("Нравится ли вам наука и техника?");
        Question.add("Интересуетесь ли вы бизнесом и компаниями?");
        Question.add("Интересуетесь ли вы музыкой?");
        Question.add("Интересуетесь ли вы путешествиями?");
        Question.add("Интересуетесь ли вы спортом?");

    }

    void setOproc(Long chatId, int ques){
        SendMessage message = new SendMessage();
        InlineKeyboardMarkup mark = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton buta = new InlineKeyboardButton();
        buta.setText("Нет");
        buta.setCallbackData("No " + ques);
        buttons.add(buta);
        InlineKeyboardButton buta1 = new InlineKeyboardButton();
        buta1.setText("Возможно");
        buta1.setCallbackData("Maybe " + ques);
        buttons.add(buta1);
        InlineKeyboardButton buta2 = new InlineKeyboardButton();
        buta2.setText("Да");
        buta2.setCallbackData("Yes " + ques);

        buta2.setCallbackGame(new CallbackGame());
        buttons.add(buta2);

        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(buttons);
        mark.setKeyboard(list);
        message.setReplyMarkup(mark);
        message.setText(Question.get(ques));
        message.setChatId(chatId);
        try{
            execute(message);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasCallbackQuery()){
            try {
                DataBase.ReadDB();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            int number = Integer.parseInt(update.getCallbackQuery().getData().split(" ")[1]);
            try {
                DataBase.setInfoIntoDatabase(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery());
            }catch (Exception ex){
                ex.printStackTrace();
            }
            if (number < Question.size()-1)
                setOproc(update.getCallbackQuery().getMessage().getChatId(), number+1);
            else
            {


                SendMessage message = new SendMessage();
                message.setText("Настройки");
                message.setChatId(update.getCallbackQuery().getMessage().getChatId());
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                replyKeyboardMarkup.setOneTimeKeyboard(false);

                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow keyboardFirstRow = new KeyboardRow();
                keyboardFirstRow.add("Update news");
                KeyboardRow keyboardSecondRow = new KeyboardRow();
                keyboardSecondRow.add("settings");
                keyboardSecondRow.add("rate us");
                keyboard.add(keyboardFirstRow);
                keyboard.add(keyboardSecondRow);
                replyKeyboardMarkup.setKeyboard(keyboard);
                message.setReplyMarkup(replyKeyboardMarkup);

                try {
                    // Send the message
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                /*
                System.out.println("it has callbackquery");
                SendMessage mess = new SendMessage()
                        .setChatId(update.getCallbackQuery().getMessage().getChatId())
                        .setText(update.getCallbackQuery().getData());
                try {
                    execute(mess);
                }catch (Exception ex){
                    ex.printStackTrace();
                }*/
            }
        }
        else {
            try {
                    if (update.getMessage().getText().equals("Update news")) {
                        VKNewsRequest tmp = new VKNewsRequest();
                        try {
                            VKNames[] arr = {POLITICS, KINO, NAUKA_I_TECHNICA, CORPORATIONS_I_FIRMS,
                                    MUSICA, PUTESHESTVIJA, SPORT};
                            String[] interests = DataBase.getInterest(update.getMessage().getChatId()).split(";");
                            ArrayList<News> news = new ArrayList<>(0);
                            System.out.println("size" + interests.length);
                            for(int i = 0; i < interests.length;i++)
                                if (interests[i].equals("1"))
                                    news.addAll(tmp.getVKNews(arr[i], 100, 4));
                            else
                                if (interests[i].equals("0"))
                                    news.addAll(tmp.getVKNews(arr[i], 100, 2));
                            UpdateNewsToChatId(news, update.getMessage().getChatId());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else
                        if(update.getMessage().getText().equals("settings") || update.getMessage().getText().equals("rate us")){}
                        else{
                        try {
                            if (DataBase.getAllChatId().indexOf(update.getMessage().getChatId()) == -1) {
                                setOproc(update.getMessage().getChatId(), 0);
                                DataBase.WriteDB(update);
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }} catch(Exception ex){
                    ex.printStackTrace();
                }
        }
    }
}