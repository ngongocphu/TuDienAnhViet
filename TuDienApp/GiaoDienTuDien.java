package TuDienApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class GiaoDienTuDien extends JFrame {
    private JTextField txtNhap;
    private JTextArea txtKetQua;
    private JButton btnSearch, btnShowAll, btnFuzzy, btnAdd, btnUpdate, btnDelete, btnExit;

    private MayKhachTuDien client;
    private String role;

    public GiaoDienTuDien(MayKhachTuDien client, String role) {
        this.client = client;
        this.role = role;

        setTitle("Từ điển - " + role.toUpperCase());
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        txtNhap = new JTextField();
        topPanel.add(txtNhap, BorderLayout.CENTER);

        btnSearch = new JButton("Tra từ");
        btnShowAll = new JButton("Hiện tất cả");
        btnFuzzy = new JButton("Tìm gần đúng");

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(btnSearch);
        btnPanel.add(btnShowAll);
        btnPanel.add(btnFuzzy);

        if (role.equals("admin")) {
            btnAdd = new JButton("Thêm từ");
            btnUpdate = new JButton("Sửa nghĩa");
            btnDelete = new JButton("Xóa từ");
            btnPanel.add(btnAdd);
            btnPanel.add(btnUpdate);
            btnPanel.add(btnDelete);
        }

        btnExit = new JButton("Thoát");
        btnPanel.add(btnExit);

        txtKetQua = new JTextArea();
        txtKetQua.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtKetQua);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // Gắn sự kiện
        btnSearch.addActionListener(e -> guiLenh("SEARCH " + txtNhap.getText()));
        btnShowAll.addActionListener(e -> guiLenh("SHOWALL"));
        btnFuzzy.addActionListener(e -> guiLenh("FUZZY " + txtNhap.getText()));
        if (role.equals("admin")) {
            btnAdd.addActionListener(e -> guiLenh("ADD " + txtNhap.getText()));
            btnUpdate.addActionListener(e -> guiLenh("UPDATE " + txtNhap.getText()));
            btnDelete.addActionListener(e -> guiLenh("DELETE " + txtNhap.getText()));
        }
        btnExit.addActionListener(e -> {
            guiLenh("EXIT");
            System.exit(0);
        });
    }

    private void guiLenh(String lenh) {
        try {
            client.guiLenh(lenh);
            String resp = client.nhanPhanHoi();
            txtKetQua.append(resp + "\n");
        } catch (IOException ex) {
            txtKetQua.append("⚠ Lỗi kết nối tới server!\n");
        }
    }	 
}
