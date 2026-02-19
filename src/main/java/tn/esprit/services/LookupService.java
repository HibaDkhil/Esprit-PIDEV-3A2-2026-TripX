package tn.esprit.services;

import tn.esprit.entities.*;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ---- These 4 tables are prefilled and managed by teammates ----
// ---- Admin just reads from them when creating a Pack       ----

public class LookupService {

    private Connection conx;

    public LookupService() {
        conx = MyDatabase.getInstance().getConx();
    }

    // ---- Destinations ----
    public List<Destination> getAllDestinations() throws SQLException {
        List<Destination> list = new ArrayList<>();
        ResultSet res = conx.createStatement().executeQuery("SELECT * FROM `destinations`");
        while (res.next()) {
            list.add(new Destination(res.getInt("id_destination"), res.getString("name")));
        }
        return list;
    }

    public Destination getDestinationById(int id) throws SQLException {
        PreparedStatement pstm = conx.prepareStatement("SELECT * FROM `destinations` WHERE `id_destination` = ?");
        pstm.setInt(1, id);
        ResultSet res = pstm.executeQuery();
        if (res.next()) return new Destination(res.getInt("id_destination"), res.getString("name"));
        return null;
    }

    // ---- Activities ----
    public List<Activity> getAllActivities() throws SQLException {
        List<Activity> list = new ArrayList<>();
        ResultSet res = conx.createStatement().executeQuery("SELECT * FROM `activities`");
        while (res.next()) {
            list.add(new Activity(res.getInt("id_activity"), res.getString("name")));
        }
        return list;
    }

    public Activity getActivityById(int id) throws SQLException {
        PreparedStatement pstm = conx.prepareStatement("SELECT * FROM `activities` WHERE `id_activity` = ?");
        pstm.setInt(1, id);
        ResultSet res = pstm.executeQuery();
        if (res.next()) return new Activity(res.getInt("id_activity"), res.getString("name"));
        return null;
    }

    // ---- Accommodations ----
    public List<Accommodation> getAllAccommodations() throws SQLException {
        List<Accommodation> list = new ArrayList<>();
        ResultSet res = conx.createStatement().executeQuery("SELECT * FROM `accommodations`");
        while (res.next()) {
            list.add(new Accommodation(res.getInt("id_accommodation"), res.getString("name")));
        }
        return list;
    }

    public Accommodation getAccommodationById(int id) throws SQLException {
        PreparedStatement pstm = conx.prepareStatement("SELECT * FROM `accommodations` WHERE `id_accommodation` = ?");
        pstm.setInt(1, id);
        ResultSet res = pstm.executeQuery();
        if (res.next()) return new Accommodation(res.getInt("id_accommodation"), res.getString("name"));
        return null;
    }

    // ---- Transport ----
    public List<Transport> getAllTransport() throws SQLException {
        List<Transport> list = new ArrayList<>();
        ResultSet res = conx.createStatement().executeQuery("SELECT * FROM `transport`");
        while (res.next()) {
            list.add(new Transport(res.getInt("id_transport"), res.getString("type")));
        }
        return list;
    }

    public Transport getTransportById(int id) throws SQLException {
        PreparedStatement pstm = conx.prepareStatement("SELECT * FROM `transport` WHERE `id_transport` = ?");
        pstm.setInt(1, id);
        ResultSet res = pstm.executeQuery();
        if (res.next()) return new Transport(res.getInt("id_transport"), res.getString("type"));
        return null;
    }
}
