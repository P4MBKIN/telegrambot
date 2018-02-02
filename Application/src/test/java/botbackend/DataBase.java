package botbackend;
import org.telegram.telegrambots.api.objects.Update;
import java.sql.*;

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
        statmt.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text, 'tgid' INT);");

        System.out.println("Таблица создана или уже существует.");
    }

    // --------Заполнение таблицы--------
    public static void WriteDB(Update update) throws SQLException
    {
        try
        {
            String name = update.getMessage().getChat().getFirstName();
            int tgid = update.getMessage().getChatId().intValue();
            if (name != null)
            {
                if (statmt.execute("SELECT EXISTS (SELECT * FROM users WHERE 'tgid' = tgid LIMIT = 1);") == Boolean.FALSE) {
                    PreparedStatement st = conn.prepareStatement("INSERT INTO 'users' ('name', 'tgid') VALUES (?, ?)");
                    st.setString(1, name);
                    st.setInt(2, tgid);
                }
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
            String  tgid = resSet.getString("tgid");
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