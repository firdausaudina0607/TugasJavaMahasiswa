package javadatabase;

import java.io.*;
import java.sql.*;
import java.util.*;

public class MahasiswaDAO {

    private Connection conn;

    public MahasiswaDAO(Connection conn) {
        this.conn = conn;
    }

    public void insert(Mahasiswa mhs) {
        String sql = "INSERT INTO mahasiswa (nama, nim, jenis_mahasiswa, jumlah_sks, biaya_kuliah) VALUES (?, ?, ?, ?, ?)";
        try ( Connection con = DbConnection.connect();  PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mhs.getNama());
            ps.setString(2, mhs.getNim());
            ps.setString(3, mhs.getJenisMahasiswa());
            ps.setInt(4, mhs.getJumlahSks());
            ps.setLong(5, mhs.hitungBiaya());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Mahasiswa mhs) {
        String sql = "UPDATE mahasiswa SET nama=?, nim=?, jenis_mahasiswa = ?, jumlah_sks = ?, biaya_kuliah = ?  WHERE id=?";
        try ( Connection con = DbConnection.connect();  PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mhs.getNama());
            ps.setString(2, mhs.getNim());
            ps.setString(3, mhs.getJenisMahasiswa());
            ps.setInt(4, mhs.getJumlahSks());
            ps.setLong(5, mhs.hitungBiaya());
            ps.setInt(6, mhs.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM mahasiswa WHERE id=?";
        try ( Connection con = DbConnection.connect();  PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Mahasiswa> getAll() {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa ORDER BY id ASC";

        try ( Connection con = DbConnection.connect();  Statement st = con.createStatement();  ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Mahasiswa mhs = new Mahasiswa(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("nim"),
                        rs.getString("jenis_mahasiswa"),
                        rs.getInt("jumlah_sks")
                );

                // set biaya kuliah dari database
                mhs.setBiayaKuliah(rs.getLong("biaya_kuliah"));

                list.add(mhs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void uploadCSV(File file) throws Exception {
        try ( BufferedReader br = new BufferedReader(new FileReader(file))) {
            Connection con = DbConnection.connect();
            String line;

            // Lewati header
            br.readLine();

            String sql = "INSERT INTO mahasiswa (nama, nim, jenis_mahasiswa, jumlah_sks, biaya_kuliah) "
                    + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");

                // harus ada 4 kolom
                if (data.length < 4) {
                    continue;
                }

                String nama = data[0].trim();
                String nim = data[1].trim();
                String jenis = data[2].trim();
                int sks = Integer.parseInt(data[3].trim());

                // Buat objek mahasiswa sesuai jenis
                Mahasiswa mhs;

                switch (jenis) {
                    case "Reguler":
                        mhs = new MahasiswaReguler(nama, nim, sks);
                        break;

                    case "Beasiswa":
                        mhs = new MahasiswaBeasiswa(nama, nim, sks);
                        break;

                    default:
                        mhs = new MahasiswaInternasional(nama, nim, sks);
                        break;
                }

                long biaya = mhs.hitungBiaya();  // hitung otomatis biaya kuliah

                pst.setString(1, nama);
                pst.setString(2, nim);
                pst.setString(3, jenis);
                pst.setInt(4, sks);
                pst.setLong(5, biaya);

                pst.addBatch();
            }

            pst.executeBatch();
            pst.close();
            con.close();

        } catch (Exception e) {
            throw new Exception("Gagal upload CSV: " + e.getMessage());
        }
    }

}
