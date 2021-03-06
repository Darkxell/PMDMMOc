package com.darkxell.model.ejb;

import com.darkxell.common.dbobject.DBPlayer;
import com.darkxell.common.dbobject.DatabaseIdentifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

/**
 *
 * @author Darkxell
 */
@Stateless
@LocalBean
public class PlayerDAO {

    @Resource(mappedName = "jdbc/pmdmmodatabase")
    private DataSource ds;

    /**
     * Creates a new player in the database
     *
     * @return the id of the player created
     */
    public long create(DBPlayer player) {
        try {
            // ID AUTOINCREMENT CODE
            Connection cx = ds.getConnection();
            ResultSet result = cx
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeQuery(
                            "SELECT MAX(id) as id FROM player"
                    );
            cx.close();
            if (result.first()) {
                // Check if there isn't a player already nammed like this
                if (this.find(player.name) != null) {
                    System.out.println("Refused player creation: " + player.name + " is already a used name in the database.");
                    return 0;
                }
                // ADD THE PLAYER
                long newid = result.getLong("id") + 1;
                Connection cn = ds.getConnection();
                PreparedStatement prepare
                        = cn.prepareStatement(
                                "INSERT INTO player (moneyinbank, moneyinbag, name, passhash, id, storyposition, isop, isbanned) VALUES(?, ?, ?, ?, ?, ?, ?, ?)"
                        );
                prepare.setLong(1, player.moneyinbank);
                prepare.setLong(2, player.moneyinbag);
                prepare.setString(3, player.name);
                prepare.setString(4, player.passhash);
                prepare.setLong(5, newid);
                prepare.setLong(6, player.storyposition);
                // Hardcoded false op and banned to prevent an eventual exploit
                prepare.setBoolean(7, false);
                prepare.setBoolean(8, false);
                prepare.executeUpdate();
                cn.close();
                return newid;
            } else {
                System.err.println("Could not autoincrement properly.");
            }
        } catch (Exception e) {
            System.err.println("Error while trying to add a new  player the the database.");
            e.printStackTrace();
        }
        return 0;
    }

    public void delete(DBPlayer player) {
        try {
            Connection cn = ds.getConnection();
            cn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            ).executeUpdate(
                    "DELETE FROM player WHERE id = " + player.id
            );
            cn.close();
            System.out.print("Player " + player.name + " has been deleted from the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DBPlayer find(long id) {
        DBPlayer toreturn = null;
        try {
            Connection cn = ds.getConnection();
            ResultSet result = cn
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeQuery(
                            "SELECT * FROM player WHERE id = " + id
                    );
            if (result.first()) {
                toreturn = new DBPlayer(id, result.getString("name"), result.getString("passhash"),
                        result.getLong("moneyinbank"), result.getLong("moneyinbag"), result.getInt("storyposition"),
                        null, null, null, null, null, null, result.getInt("points"), result.getBoolean("isop"), result.getBoolean("isbanned"));
            }
            cn.close();
            completefind(toreturn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toreturn;
    }

    public DBPlayer find(String name) {
        DBPlayer toreturn = null;
        if (name == null || name.equals("")) {
            return toreturn;
        }
        try {
            Connection cn = ds.getConnection();
            String selectSQL = "SELECT * FROM player WHERE name = ?";
            PreparedStatement preparedStatement = cn.prepareStatement(selectSQL);
            preparedStatement.setString(1, name);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                toreturn = new DBPlayer(result.getLong("id"), result.getString("name"), result.getString("passhash"),
                        result.getLong("moneyinbank"), result.getLong("moneyinbag"), result.getInt("storyposition"),
                        null, null, null, null, null, null, result.getInt("points"), result.getBoolean("isop"), result.getBoolean("isbanned"));
            }
            cn.close();
            if (toreturn != null) {
                completefind(toreturn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toreturn;
    }

    /**
     * Completes a DBPlayer object with all the IDs from the different tables.
     */
    private void completefind(DBPlayer toreturn) {
        try {
            //Select pokemon in team
            Connection cn = ds.getConnection();
            String selectSQL = "SELECT * FROM teammember_ WHERE playerid = ? AND location >= 2";
            PreparedStatement preparedStatement = cn.prepareStatement(selectSQL);
            preparedStatement.setLong(1, toreturn.id);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                if (toreturn.pokemonsinparty == null) {
                    toreturn.pokemonsinparty = new ArrayList<>(4);
                }
                toreturn.pokemonsinparty.add(new DatabaseIdentifier(result.getLong("pokemonid")));
            }
            cn.close();
            //Select pokemon in zones
            cn = ds.getConnection();
            selectSQL = "SELECT * FROM teammember_ WHERE playerid = ? AND location = 0";
            preparedStatement = cn.prepareStatement(selectSQL);
            preparedStatement.setLong(1, toreturn.id);
            result = preparedStatement.executeQuery();
            while (result.next()) {
                if (toreturn.pokemonsinzones == null) {
                    toreturn.pokemonsinzones = new ArrayList<>(20);
                }
                toreturn.pokemonsinzones.add(new DatabaseIdentifier(result.getLong("pokemonid")));
            }
            cn.close();
            // Select leader pokemon
            cn = ds.getConnection();
            selectSQL = "SELECT * FROM teammember_ WHERE playerid = ? AND location = 1";
            preparedStatement = cn.prepareStatement(selectSQL);
            preparedStatement.setLong(1, toreturn.id);
            result = preparedStatement.executeQuery();
            if (result.next()) {
                toreturn.mainpokemon = new DatabaseIdentifier(result.getLong("pokemonid"));
            }
            cn.close();
            // Select toolbox inventory id
            cn = ds.getConnection();
            selectSQL = "SELECT * FROM toolbox_ WHERE playerid = ?";
            preparedStatement = cn.prepareStatement(selectSQL);
            preparedStatement.setLong(1, toreturn.id);
            result = preparedStatement.executeQuery();
            if (result.next()) {
                toreturn.toolboxinventory = new DatabaseIdentifier(result.getLong("inventoryid"));
            }
            cn.close();
            // Select storage inventory id
            cn = ds.getConnection();
            selectSQL = "SELECT * FROM playerstorage_ WHERE playerid = ?";
            preparedStatement = cn.prepareStatement(selectSQL);
            preparedStatement.setLong(1, toreturn.id);
            result = preparedStatement.executeQuery();
            if (result.next()) {
                toreturn.storageinventory = new DatabaseIdentifier(result.getLong("inventoryid"));
            }
            cn.close();
            // Select missions
            cn = ds.getConnection();
            selectSQL = "SELECT * FROM missions_ WHERE playerid = ?";
            preparedStatement = cn.prepareStatement(selectSQL);
            preparedStatement.setLong(1, toreturn.id);
            result = preparedStatement.executeQuery();
            while (result.next()) {
                if (toreturn.missionsids == null) {
                    toreturn.missionsids = new ArrayList<>();
                }
                toreturn.missionsids.add(result.getString("missionid"));
            }
            cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(DBPlayer player) {
        try {
            Connection cn = ds.getConnection();
            PreparedStatement prepare
                    = cn.prepareStatement(
                            "UPDATE player SET moneyinbank = ?, moneyinbag = ?, name = ?, passhash = ?, storyposition = ?, points = ?, isbanned = ? WHERE id = ?"
                    );
            prepare.setLong(1, player.moneyinbank);
            prepare.setLong(2, player.moneyinbag);
            prepare.setString(3, player.name);
            prepare.setString(4, player.passhash);
            prepare.setInt(5, player.storyposition);
            prepare.setInt(6, player.points);
            prepare.setBoolean(7, player.isbanned);
            prepare.setLong(8, player.id);
            prepare.executeUpdate();
            cn.close();
            //Note : isop status cannot be updtated to prevent potential exploit.
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
