package javadatabase;

import java.sql.*;
import java.util.*;

public class MahasiswaDAO {

    private Connection conn;

    public MahasiswaDAO(Connection conn) {
        this.conn = conn;
    }

   public void insert(Mahasiswa mhs) {
    String sql = "INSERT INTO mahasiswa (nama, nim) VALUES (?, ?)";
    try (Connection con = DbConnection.connect();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, mhs.getNama());
        ps.setString(2, mhs.getNim());
        ps.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    public void update(Mahasiswa mhs) {
        String sql = "UPDATE mahasiswa SET nama=?, nim=? WHERE id=?";
        try (Connection con = DbConnection.connect();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, mhs.getNama());
            pst.setString(2, mhs.getNim());
            pst.setInt(3, mhs.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM mahasiswa WHERE id=?";
        try (Connection con = DbConnection.connect();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Mahasiswa> getAll() {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa ORDER BY id ASC";
        try (Connection con = DbConnection.connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Mahasiswa mhs    = new Mahasiswa(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("nim")
                );
                list.add(mhs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
