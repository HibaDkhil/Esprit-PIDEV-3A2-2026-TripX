package tn.esprit;

import tn.esprit.utils.MyDB;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class GetUsers {
    public static void main(String[] args) {
        try {
            Connection con = MyDB.getInstance().getConx();
            if (con == null) {
                System.out.println("Could not connect to DB.");
                return;
            }
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, email, password FROM user LIMIT 5");
            while(rs.next()) {
                System.out.println("USER_FOUND: ID=" + rs.getInt("id") + " | Email=" + rs.getString("email") + " | Password=" + rs.getString("password"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
