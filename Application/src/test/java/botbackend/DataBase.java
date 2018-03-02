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

    /**
     * Подключает базу жанных
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void Conn() throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:newbd.s3db");
    }

    /**
     * Создает таблицу
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void CreateDB() throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'users' ('name' text, 'tgid' INT PRIMARY KEY);");
    }

    /**
     * Возвращает список всех chatId, хранящихся в базе данных
     * @return
     * @throws Exception
     */
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

    /**
     * Заполняет таблицу информацией из update
     * @param update
     * @throws SQLException
     */
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
    }

    /**
     * Заносит информацию об ответе на вопрос в базу данных
     * @param chatId
     * @param data
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void setInfoIntoDatabase(Long chatId, CallbackQuery data)throws ClassNotFoundException, SQLException{
        resSet = statmt.executeQuery("SELECT * FROM users");
        int id = -1;
        String name = "";
        while(resSet.next() && id != chatId)
        {
            id = resSet.getInt("tgid");
            name = resSet.getString("name");
        }
        if (name.length() < Integer.parseInt(data.getData().split(" ")[1])*2 + 1) {
            switch (data.getData().split(" ")[0]) {
                case "No":
                    name += ";0";
                    break;
                case "Yes":
                    name += ";2";
                    break;
                case "Maybe":
                    name += ";1";
                    break;
                default:
                    System.out.println("Unhandle exc");
                    break;
            }

            try {
                Integer number = chatId.intValue();
                if (name != null && number != null) {
                    statmt.execute("INSERT OR REPLACE INTO 'users' ('name', 'tgid') VALUES ('" + name + "','" + number + "')");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Возвращает информацию об опросе из базы данных
     * @param chatId
     * @return
     * @throws Exception
     */
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

    /**
     * ывод всей информации, хранящейся в базе данных
     * @throws ClassNotFoundException
     * @throws SQLException
     */
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
    }

    /**
     * Закрытие базы данных
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void CloseDB() throws ClassNotFoundException, SQLException
    {
        conn.close();
        statmt.close();
        resSet.close();
    }
}