package botbackend;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Update;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;


public class DataBase {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:newbd.s3db");

        System.out.println("База Подключена!");
    }

    // --------Создание таблицы--------
    public static void CreateDB() throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'users' ('name' text, 'tgid' INT PRIMARY KEY);");

        System.out.println("Таблица создана или уже существует.");
    }

    //Возвращает список id в базе данных
    public static List<Integer> getAllChatId() throws Exception{
        List<Integer> ids = new ArrayList<>(0);
        resSet = statmt.executeQuery("SELECT * FROM users");
        while(resSet.next())
        {
            int  tgid = resSet.getInt("tgid");
            System.out.println(tgid + "tgid");
            ids.add(tgid);
        }
        return ids;
    }

    // --------Заполнение таблицы--------
    public static void WriteDB(Update update) throws SQLException
    {
        try
        {
            String name = "";
            Integer number = update.getMessage().getChatId().intValue();
            if (name != null && number!=null)
                    statmt.execute("INSERT OR REPLACE INTO 'users' ('name', 'tgid') VALUES ('" + name + "','" + number +"')");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        System.out.println("Таблица заполнена");
    }

    public static void setInfoIntoDatabase(Long chatId, CallbackQuery data)throws ClassNotFoundException, SQLException{
        System.out.println("allo");
        resSet = statmt.executeQuery("SELECT * FROM users");
        int id = -1;
        String name = "";
        while(resSet.next() && id != chatId)
        {
            id = resSet.getInt("tgid");
            name = resSet.getString("name");
        }
        switch (data.getData().split(" ")[0]) {
            case "No":
                name += ";-1";
                break;
            case "Yes":
                name += ";1";
                break;
            case "Maybe":
                name += ";0";
                break;
                default:
                    System.out.println("Unhandle exc");
                    break;

        }
        try
        {
            Integer number = chatId.intValue();
            if (name != null && number!=null)
            {
                statmt.execute("INSERT OR REPLACE INTO 'users' ('name', 'tgid') VALUES ('" + name +"','" + number +"')");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public static String getInterest(Long chatId) throws Exception{
        resSet = statmt.executeQuery("SELECT * FROM users");
        String name = "";
        while(resSet.next())
        {
            name = resSet.getString("name");
            int tgid = resSet.getInt("tgid");
            if (tgid == chatId)
                break;
        }
        return name;
    }

    // -------- Вывод таблицы--------
    public static void ReadDB() throws ClassNotFoundException, SQLException
    {
        resSet = statmt.executeQuery("SELECT * FROM users");

        while(resSet.next())
        {
            String  name = resSet.getString("name");
            int  tgid = resSet.getInt("tgid");
            System.out.println( "name = " + name );
            System.out.println( "tgid = " + tgid );
            System.out.println();
        }

        System.out.println("Таблица выведена");
    }

    // --------Закрытие--------
    public static void CloseDB() throws ClassNotFoundException, SQLException
    {
        conn.close();
        statmt.close();
        resSet.close();

        System.out.println("Соединения закрыты");
    }

}