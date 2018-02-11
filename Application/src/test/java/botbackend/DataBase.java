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
        statmt.execute("CREATE TABLE if not exists 'users' ('name' text, 'tgid' INT PRIMARY KEY, 'resultsofoproc' text);");

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
            String name = update.getMessage().getChat().getFirstName();
            Integer number = update.getMessage().getChatId().intValue();
            String oproc = "";
            if (name != null && number!=null)
                    statmt.execute("INSERT OR REPLACE INTO 'users' ('name', 'tgid', 'resultofoproc') VALUES ('" + name +"','" + number + "','" + oproc +"')");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        System.out.println("Таблица заполнена");
    }

    public static void setInfoIntoDatabase(Long chatId, CallbackQuery data)throws ClassNotFoundException, SQLException{
        System.out.println("allo");
        resSet = statmt.executeQuery("SELECT * FROM users");
        int id = -1;
        String name;
        String oproc = "";
        while(resSet.next() && id != chatId)
        {
            id = resSet.getInt("tgid");
            oproc = resSet.getString("resultsofoproc");
        }
        switch (data.getData().split(" ")[0]) {
            case "No":
                oproc += ";-1";
                break;
            case "Yes":
                oproc += ";1";
                break;
            case "Maybe":
                oproc += ";0";
                break;
                default:
                    System.out.println("Unhandle exc");
                    break;

        }
        try
        {
            name = data.getFrom().getFirstName();
            Integer number = chatId.intValue();
            if (name != null && number!=null)
            {
                System.out.println("what" + statmt.execute("SELECT EXISTS(SELECT * FROM 'users' WHERE 'tgid' = '" + number + "' LIMIT 1);"));
                //if (statmt.execute("SELECT EXISTS(SELECT * FROM 'users' WHERE 'tgid' = '\" + number + \"' LIMIT 1);") == Boolean.TRUE) {
                statmt.execute("INSERT OR REPLACE INTO 'users' ('name', 'tgid') VALUES ('" + name +"','" + number +"')");
                //st.setString(1, name);
                //st.setInt(2, number);

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }


    // -------- Вывод таблицы--------
    public static void ReadDB() throws ClassNotFoundException, SQLException
    {
        resSet = statmt.executeQuery("SELECT * FROM users");

        while(resSet.next())
        {
            int id = resSet.getInt("id");
            String  name = resSet.getString("name");
            int  tgid = resSet.getInt("tgid");
            System.out.println( "ID = " + id );
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