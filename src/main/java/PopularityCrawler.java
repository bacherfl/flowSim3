import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Created by florian on 05.11.2015.
 */
public class PopularityCrawler {

    static Connection conn;

    public static void main(String[] args) {
        try {
            conn = getConnection();

            retrievePopularities();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void retrievePopularities() {
        try {
            conn.setCatalog("youtube-dataset");
            TreeMap<String, Integer> tmpViews = new TreeMap<>();
            FileWriter fw = new FileWriter("popularities2.txt");
            int hour = 0;
            Statement stmt = null;

            String query = "SELECT count(*) AS total FROM entry_history;";

            int total = 0;
            try {
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    total = rs.getInt("total");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            int processedRows = 0;
            while ((processedRows < total) && (hour <= 3000)) {
                query = "select e.id, eh.viewCount from entry e \n" +
                        "LEFT OUTER JOIN entry_history eh ON e.id=eh.entry_id\n" +
                        "WHERE eh.history_timestamp > \n" +
                        "(SELECT DATE_ADD(min(history_timestamp), INTERVAL " +  hour + " HOUR) FROM entry_history)" +
                        "AND eh.history_timestamp <= " +
                        "(SELECT DATE_ADD(min(history_timestamp), INTERVAL " +  (hour + 1) + " HOUR) FROM entry_history);";

                try {
                    fw.write("Hour-" + hour + "\n");
                    stmt = conn.createStatement();

                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        String id = rs.getString("id");
                        Integer viewCount = rs.getInt("viewCount");
                        if (tmpViews.get(id) == null) {
                            tmpViews.put(id, viewCount);
                        } else {
                            int viewCountDelta = viewCount - tmpViews.get(id);
                            fw.write(id + " " + viewCountDelta + "\n");
                        }
                        processedRows++;
                    }
                    hour++;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            fw.close();
        } catch (IOException e) {

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection conn;

        Properties connectionProps = new Properties();

        connectionProps.put("user", "root");
        connectionProps.put("password", "");

        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", connectionProps);

        return conn;
    }
}
