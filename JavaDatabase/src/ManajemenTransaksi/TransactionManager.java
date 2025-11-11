/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ManajemenTransaksi;

/**
 *
 * @author ASUS
 */
import java.sql.*;

public class TransactionManager {
  
    public static void transferSaldo(int pengirimId, int penerimaId, double jumlah) throws SQLException {
        Connection conn = null;
        PreparedStatement psUpdatePengirim = null;
        PreparedStatement psUpdatePenerima = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi   
// Cek saldo pengirim
            if (!cekSaldoCukup(conn, pengirimId, jumlah)) {
                throw new SQLException("Saldo pengirim tidak mencukupi.");
            }
// Update saldo pengirim
            String sqlUpdatePengirim = "UPDATE akun SET saldo = saldo - ? WHERE id = ?";
            psUpdatePengirim = conn.prepareStatement(sqlUpdatePengirim);
            psUpdatePengirim.setDouble(1, jumlah);
            psUpdatePengirim.setInt(2, pengirimId);
            psUpdatePengirim.executeUpdate();
// Update saldo penerima
            String sqlUpdatePenerima = "UPDATE akun SET saldo = saldo + ? WHERE id = ?";
            psUpdatePenerima = conn.prepareStatement(sqlUpdatePenerima);
            psUpdatePenerima.setDouble(1, jumlah);
            psUpdatePenerima.setInt(2, penerimaId);
            psUpdatePenerima.executeUpdate();
            conn.commit(); // Komit transaksi
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback jika ada error
            }
            throw e;
        } finally {
            if (psUpdatePengirim != null) {
                psUpdatePengirim.close();
            }
            if (psUpdatePenerima != null) {
                psUpdatePenerima.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    private static boolean cekSaldoCukup(Connection conn, int pengirimId, double jumlah) throws SQLException {
        String sqlCekSaldo = "SELECT saldo FROM akun WHERE id = ?";
        PreparedStatement psCekSaldo = conn.prepareStatement(sqlCekSaldo);
        psCekSaldo.setInt(1, pengirimId);
        ResultSet rs = psCekSaldo.executeQuery();
        if (rs.next()) {
            double saldo = rs.getDouble("saldo");
            return saldo >= jumlah;
        }
        return false;
    }
}
