/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lokasi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author This PC
 */
public class Lokasi extends JFrame {
    private Connection conn;
    private DefaultTableModel tableModel;
    private JTable lokasiTable;

    public Lokasi() {
        setTitle("Form Lokasi");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/manajemenprop", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        tableModel = new DefaultTableModel(new String[]{"ID", "Nama Lokasi"}, 0);
        lokasiTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(lokasiTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnTambah = new JButton("Tambah");
        btnTambah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tambahLokasi();
            }
        });
        buttonPanel.add(btnTambah);

        JButton btnEdit = new JButton("Edit");
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editLokasi();
            }
        });
        buttonPanel.add(btnEdit);

        JButton btnHapus = new JButton("Hapus");
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hapusLokasi();
            }
        });
        buttonPanel.add(btnHapus);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        try {
            PreparedStatement statement = conn.prepareStatement("SELECT id, nama_lokasi FROM lokasi");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String namaLokasi = resultSet.getString("nama_lokasi");
                tableModel.addRow(new Object[]{id, namaLokasi});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tambahLokasi() {
        String namaLokasi = JOptionPane.showInputDialog(this, "Masukkan Nama Lokasi:");
        if (namaLokasi != null && !namaLokasi.isEmpty()) {
            try {
                PreparedStatement statement = conn.prepareStatement("INSERT INTO lokasi (nama_lokasi) VALUES (?)");
                statement.setString(1, namaLokasi);
                statement.executeUpdate();
                refreshTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void editLokasi() {
        int selectedRow = lokasiTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) lokasiTable.getValueAt(selectedRow, 0);
            String namaLokasiBaru = JOptionPane.showInputDialog(this, "Edit Nama Lokasi:", lokasiTable.getValueAt(selectedRow, 1));
            if (namaLokasiBaru != null && !namaLokasiBaru.isEmpty()) {
                try {
                    PreparedStatement statement = conn.prepareStatement("UPDATE lokasi SET nama_lokasi = ? WHERE id = ?");
                    statement.setString(1, namaLokasiBaru);
                    statement.setInt(2, id);
                    statement.executeUpdate();
                    refreshTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang akan di-edit.");
        }
    }

    private void hapusLokasi() {
        int selectedRow = lokasiTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) lokasiTable.getValueAt(selectedRow, 0);
            int confirmResult = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirmResult == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement statement = conn.prepareStatement("DELETE FROM lokasi WHERE id = ?");
                    statement.setInt(1, id);
                    statement.executeUpdate();
                    refreshTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang akan dihapus.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Lokasi().setVisible(true);
        });
    }
}