/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.darkxell.model.ejb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class Learnedmove_DAO {

    @Resource(mappedName = "jdbc/pmdmmodatabase")
    private DataSource ds;

    public void create(long pokemonid, long moveid) {
        try {
            Connection cn = ds.getConnection();
            PreparedStatement prepare
                    = cn.prepareStatement(
                            "INSERT INTO learnedmove_ (pokemonid, moveid) VALUES (?, ?)"
                    );
            prepare.setLong(1, pokemonid);
            prepare.setLong(2, moveid);
            prepare.executeUpdate();
            cn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(long pokemonid, long moveid) {
        try {
            Connection cn = ds.getConnection();
            cn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            ).executeUpdate(
                    "DELETE FROM learnedmove_ WHERE pokemonid = " + pokemonid + " AND moveid = " + moveid
            );
            cn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long findPokemonID(long moveid) {
        long toreturn = 0;
        try {
            Connection cn = ds.getConnection();
            ResultSet result = cn
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeQuery(
                            "SELECT * FROM learnedmove_ WHERE moveid = " + moveid
                    );
            if (result.first()) {
                toreturn = result.getLong("pokemonid");
            }
            cn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toreturn;
    }

    public long findMoveID(long pokemonid) {
        long toreturn = 0;
        try {
            Connection cn = ds.getConnection();
            ResultSet result = cn
                    .createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE
                    ).executeQuery(
                            "SELECT * FROM learnedmove_ WHERE pokemonid = " + pokemonid
                    );
            if (result.first()) {
                toreturn = result.getLong("moveid");
            }
            cn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toreturn;
    }
}
