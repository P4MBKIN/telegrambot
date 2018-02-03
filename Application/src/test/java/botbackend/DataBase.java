package botbackend;
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
        statmt.execute("CREATE TABLE if not exists 'users' ('id' INTEGER, 'name' text, 'tgid' INT PRIMARY KEY);");

        System.out.println("Таблица создана или уже существует.");
    }

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

        System.out.println("Таблица заполнена");
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