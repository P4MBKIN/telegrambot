package botbackend;


import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import static botbackend.VKNames.*;

public class Bot extends TelegramLongPollingBot{
    List<String> Question = new ArrayList<>();
    Hashtable<Long, NewsApi> usersChoice = new Hashtable<>(0);
    Hashtable<Long, Integer> userChoicenumber = new Hashtable<>(0);
    VKNames[] arr = {POLITICS, KINO, NAUKA_I_TECHNICA, CORPORATIONS_I_FIRMS, MUSICA, PUTESHESTVIJA, SPORT};
    RSSNames[] arr1 = {RSSNames.POLITICS, RSSNames.KINO, RSSNames.NAUKA_I_TECHNICA, RSSNames.CORPORATIONS_I_FIRMS, RSSNames.MUSICA, RSSNames.PUTESHESTVIJA, RSSNames.SPORT};

    @Override
    public String getBotUsername() {
        return "AntiHeapbot";
    }

    @Override
    public String getBotToken() {
        return "531412915:AAGIMCbWWt7kL-AtOTf5IuA4IWOFjrKdnWM";
    }

    /**
     * Метод, отправляюший новости по заданному чат id
     * @param news arraylist с новостями
     * @param chatId номер чата
     */
    public void UpdateNewsToChatId(ArrayList<News> news, Long chatId) {
        for (int j = 0; j < news.size(); j++) {
            System.out.println(news.size() + "allo");
            try {
                BufferedImage img = news.get(j).getAllNewsPicture();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(img, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                SendPhoto photo = new SendPhoto()
                        .setChatId(chatId)
                        .setNewPhoto("newnews", is)
                        .setReplyMarkup(createKeyboard(news.get(j).getLinkPost()));

                sendPhoto(photo);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Метод для создания ссылки на новость под фото
     * @param link
     * @return
     */
    InlineKeyboardMarkup createKeyboard(String link){
        InlineKeyboardMarkup mark = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton but = new InlineKeyboardButton()
                .setText("Ссылка")
                .setUrl(link);
        buttons.add(but);
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(buttons);
        mark.setKeyboard(list);
        return mark;
    }

    /**
     * Конструктор
     */
    public Bot(){
        VKNewsRequest a = new VKNewsRequest();
        try {
            System.out.println(a.getVKNews(VKNames.NAUKA_I_TECHNICA, 100, 4).size());
            System.out.println(a.getVKNews(VKNames.NAUKA_I_TECHNICA, 100, 4).size());
            System.out.println(a.getVKNews(VKNames.NAUKA_I_TECHNICA, 100, 4).size());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println("adsgasdgdsagdsg");
        Question.add("Интересуетесь ли вы политикой?");
        Question.add("Нравится ли вам кино?");
        Question.add("Нравится ли вам наука и техника?");
        Question.add("Интересуетесь ли вы бизнесом и компаниями?");
        Question.add("Интересуетесь ли вы музыкой?");
        Question.add("Интересуетесь ли вы путешествиями?");
        Question.add("Интересуетесь ли вы спортом?");

    }

    /**
     * Метод для создания вопроса
     * @param ques Номер вопроса
     * @return
     */
    InlineKeyboardMarkup createKeyboardForQues(int ques){
        InlineKeyboardMarkup mark = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton but = new InlineKeyboardButton()
                .setText("Нет")
                .setCallbackData("No " + ques);
        buttons.add(but);

        InlineKeyboardButton but1 = new InlineKeyboardButton()
                .setText("Возможно")
                .setCallbackData("Maybe " + ques);
        buttons.add(but1);

        InlineKeyboardButton but2 = new InlineKeyboardButton()
                .setText("Да")
                .setCallbackData("Yes " + ques);
        buttons.add(but2);

        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(buttons);
        mark.setKeyboard(list);
        return mark;
    }

    /**
     * Метод для проведения опроса
     * @param chatId номер чата
     * @param ques номер вопроса в массиве Question
     */
    void setQues(Long chatId, int ques){
        SendMessage message = new SendMessage()
                .setReplyMarkup(createKeyboardForQues(ques))
                .setText(Question.get(ques))
                .setChatId(chatId);
        try{
            execute(message);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Обрабатываем входяший запрос при опросе
     * @param update
     */
    void workWithCallbackQuery(Update update){
        /*try {
            DataBase.ReadDB();
        }catch (Exception ex){
            ex.printStackTrace();
        }*/
        int number = Integer.parseInt(update.getCallbackQuery().getData().split(" ")[1]);
        try {
            DataBase.setInfoIntoDatabase(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        if (number < Question.size()-1) {
            if (number == userChoicenumber.get(update.getCallbackQuery().getMessage().getChatId())) {
                userChoicenumber.put(update.getCallbackQuery().getMessage().getChatId(), userChoicenumber.get(update.getCallbackQuery().getMessage().getChatId()) + 1);
                setQues(update.getCallbackQuery().getMessage().getChatId(), userChoicenumber.get(update.getCallbackQuery().getMessage().getChatId()));
            }
        }
        else {
            SendMessage message = new SendMessage()
                    .setText("Настройки")
                    .setChatId(update.getCallbackQuery().getMessage().getChatId());
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup()
                    .setSelective(true)
                    .setResizeKeyboard(true)
                    .setOneTimeKeyboard(false);

            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardFirstRow = new KeyboardRow();
            keyboardFirstRow.add("Обновить новости");
            KeyboardRow keyboardSecondRow = new KeyboardRow();
            keyboardSecondRow.add("Настройки");
            keyboard.add(keyboardFirstRow);
            keyboard.add(keyboardSecondRow);
            replyKeyboardMarkup.setKeyboard(keyboard);
            message.setReplyMarkup(replyKeyboardMarkup);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Обрабатываем входящее сообщение от пользователя
     * @param update
     */
    void sendNews(Update update){
        try {
            if (update.getMessage().getText().equals("Обновить новости")) {
                VKNewsRequest tmp = usersChoice.get(update.getMessage().getChatId()).vk;
                RSSNewsRequest rss = usersChoice.get(update.getMessage().getChatId()).rss;
                try {
                    String[] interests = DataBase.getInterest(update.getMessage().getChatId()).split(";");
                    ArrayList<News> news = new ArrayList<>(0);
                    for(int i = 0; i < interests.length-1;i++)
                        if (interests[i].equals("2")){
                            news.addAll(tmp.getVKNews(arr[i], 100, 6));
                            news.addAll(rss.getRSSNews(arr1[i],100,6));}
                        else
                            if (interests[i].equals("1")){
                                news.addAll(tmp.getVKNews(arr[i], 100, 3));
                                news.addAll(rss.getRSSNews(arr1[i],100,3));}
                    UpdateNewsToChatId(news, update.getMessage().getChatId());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else
            if(update.getMessage().getText().equals("settings")){

            }
            else{
                try {
                    if (DataBase.getAllChatId().indexOf(update.getMessage().getChatId()) == -1) {
                        usersChoice.put(update.getMessage().getChatId(), new NewsApi(new VKNewsRequest(), new RSSNewsRequest()));
                        userChoicenumber.put(update.getMessage().getChatId(), 0);
                        setQues(update.getMessage().getChatId(), 0);
                        DataBase.WriteDB(update);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }} catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Метод для обработки входящих запросов к боту
     * @param update
     */
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasCallbackQuery()) {
            workWithCallbackQuery(update);
        }
        else {
            sendNews(update);
        }
    }
}