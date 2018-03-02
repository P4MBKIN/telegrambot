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
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import static botbackend.VKNames.*;

public class Bot extends TelegramLongPollingBot{
    List<String> Question = new ArrayList<>();
    Hashtable<Long, NewsApi> usersChoice = new Hashtable<>(0);
    Hashtable<Long, Integer> userChoicenumber = new Hashtable<>(0);
    VKNames[] arr = VKNames.values();
    RSSNames[] arr1 = RSSNames.values();
    int MAX_SIZE = 4;



    @Override
    public String getBotUsername() {
        return "AntiHeapbot";
    }

    @Override
    public String getBotToken() {
        return "567194667:AAF5s9I4Fo7khWgJehzfmq5tu-yGn90fv_o";
    }

    /**
     * Метод приводящий BufferedImage к InputStream
     * @param img
     * @return
     */
    InputStream buffImgIntoInputStream(BufferedImage img){
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            return is;
        }catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Метод, отправляюший новости по заданному чат id
     * @param news arraylist с новостями
     * @param chatId номер чата
     */
    public void UpdateNewsToChatId(ArrayList<News> news, Long chatId) {
        for (int j = 0; j < news.size(); j++) {
            try {
                SendPhoto photo = new SendPhoto()
                        .setChatId(chatId)
                        .setNewPhoto("newnews", buffImgIntoInputStream(news.get(j).getAllNewsPicture()))
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
        Question.add("Интересуетесь ли вы политикой?");
        Question.add("Нравится ли вам кино?");
        Question.add("Интересна ли вам наука и техника?");
        Question.add("Хотели бы вы читать о бизнесе и компаниях?");
        Question.add("Интересуетесь ли вы музыкой?");
        Question.add("Интересны ли вам путешествиями?");
        Question.add("Хотите ли вы видеть новости о спорте?");
        Question.add("Интересуетесь ли вы шоубизнесом?");
        Question.add("Хотите узнать больше о здоровье?");
        Question.add("Интересны ли вам новости о культуре?");
        Question.add("Хотите ли вы видеть новости о спорте?");
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
                int k = usersChoice.get(update.getMessage().getChatId()).koef;
                try {
                    String[] interests = DataBase.getInterest(update.getMessage().getChatId()).split(";");
                    ArrayList<News> news = new ArrayList<>(0);
                    for(int i = 1; i < interests.length;i++)
                        if (interests[i].equals("2")){
                            news.addAll(tmp.getVKNews(arr[i-1], 100, MAX_SIZE*k));
                            news.addAll(rss.getRSSNews(arr1[i-1],100,MAX_SIZE*k));}
                        else
                        if (interests[i].equals("1")){
                            news.addAll(tmp.getVKNews(arr[i-1], 100, ((MAX_SIZE)/2)*k));
                            news.addAll(rss.getRSSNews(arr1[i-1],100,((MAX_SIZE)/2)*k));}
                    usersChoice.get(update.getMessage().getChatId()).koef++;
                    UpdateNewsToChatId(news, update.getMessage().getChatId());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else
            if(update.getMessage().getText().equals("Настройки")){
                SendMessage mess = new SendMessage()
                        .setText("allo")
                        .setChatId(update.getMessage().getChatId());
                try{
                    execute(mess);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
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