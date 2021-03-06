/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.servonthetable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

/**
 *
 * @author Filip
 */
public class MySqlHelper {

    private String dbHost;
    private String dbUser;
    private String dbPass;
    private String dbName;
    private int port = 3306;
    private Connection con;
    private Statement stmt;

    /**
     *
     * @param dbHost
     * @param dbUser
     * @param dbPass
     * @param dbName
     * @param port
     */
    public MySqlHelper(String dbHost, int port, String dbUser, String dbPass, String dbName) {
        this.dbHost = dbHost;
        this.dbName = dbName;
        this.dbPass = dbPass;
        this.dbUser = dbUser;
        this.port = port;
    }

    void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = urlGenerator(this.dbHost, this.port, this.dbName);
            con = DriverManager.getConnection(url, dbUser, dbPass);
            stmt = con.createStatement();
        } catch (Exception ex) {
            Logger.getLogger(MySqlHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public List<Sheep> getSheepList(int farm_id) {
        List<Sheep> sheeps = new ArrayList<>();
        ResultSet results;

        try {
            results = stmt.executeQuery("SELECT id, farm_id, name, UNIX_TIMESTAMP(born) as born, UNIX_TIMESTAMP(deceased) as deceased, comment, weight FROM sheep_sheep WHERE farm_id = '" + farm_id + "'");
            System.out.println(results.toString());
            while (results.next()) {
                //List<SheepUpdate> updates = getSheepUpdates(results.getInt("id"), 1);
                sheeps.add(new Sheep(results.getInt("id"),
                        results.getInt("farm_id"),
                        results.getString("name"),
                        results.getInt("born"),
                        results.getInt("deceased"),
                        results.getString("comment"),
                        null,
                        results.getDouble("weight")));
            }
            results.close();

            for (Sheep sheep : sheeps) {
                List<SheepUpdate> updates = getSheepUpdates(sheep.getID(), 1);
                sheep.setUpdates(updates);
            }

            return sheeps;

        } catch (SQLException ex) {
            System.out.println(ex.toString());
            Logger.getLogger(MySqlHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<SheepUpdate> getSheepUpdates(int id, int numUpdates) {

        List<SheepUpdate> updates = new ArrayList<>();
        ResultSet results;
        try {
            String query = "SELECT id, sheep_id, UNIX_TIMESTAMP(timestamp) as timestamp, pos_x, pos_y, pulse, temp, alarm FROM sheep_updates WHERE sheep_id = '" + id + "'";
            System.out.println(query);
            results = stmt.executeQuery(query);
            while (results.next()) {
                updates.add(new SheepUpdate(results.getInt("id"),
                        results.getDouble("pos_x"),
                        results.getDouble("pos_y"),
                        results.getInt("pulse"),
                        results.getDouble("temperature"),
                        results.getInt("timestamp")));
            }
            results.close();
            return updates;
        } catch (SQLException ex) {
            Logger.getLogger(MySqlHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     * @param s
     * @return
     */
    public boolean storeNewSheep(Sheep s) {
        try {
            stmt.executeQuery("INSERT INTO sheep_sheep (farm_id, name, born, deceased, comment) VALUES '" + s.getFarmId() + "', '" + s.getName() + "', '" + s.getBorn() + "', '" + s.getDeceased() + "', '" + s.getComment() + "'");
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(MySqlHelper.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     *
     * @param s
     * @return
     */
    public boolean updateSheep(Sheep s) {
        try {
            stmt.executeQuery("UPDATE sheep_sheep SET farm_id='" + s.getFarmId() + "', name='" + s.getName() + "', born='" + s.getBorn() + "', deceased='" + s.getDeceased() + "', comment='" + s.getComment() + "' WHERE id='" + s.getID() + "'");
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(MySqlHelper.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /* removes a sheep and all updates from the database */
    public boolean removeSheep(Sheep sheep) {
        try {
            stmt.executeQuery("DELETE FROM sheep_sheep WHERE id = '" + sheep.getID() + "'");
        } catch (SQLException ex) {
            Logger.getLogger(MySqlHelper.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        try {
            stmt.executeQuery("DELETE FROM sheep_updates WHERE sheep_id = '" + sheep.getID() + "'");
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(MySqlHelper.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*  */
    public int findUser(String userName, String password) {
        try {
            String q = "SELECT id, un, pw FROM sheep_user WHERE un = '" + userName + "' AND pw = '" + password + "'";
            System.out.println(q);
            ResultSet results = stmt.executeQuery(q);

            if (results.next()) {
                return results.getInt("id");
            } else {
                return -1;
            }
        } catch (SQLException s) {
            Logger.getLogger(MySqlHelper.class.getName()).log(Level.SEVERE, null, s);
            return -1;
        }
    }

    private String urlGenerator(String host, int port, String database) {
        String url;
        url = "jdbc:mysql://" + host + ":" + Integer.toString(port) + "/" + database;
        return url;
    }

    protected void finalize() throws Throwable {
        con.close();
        super.finalize();
    }
}
