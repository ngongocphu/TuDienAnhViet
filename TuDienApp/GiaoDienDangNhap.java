package TuDienApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class GiaoDienDangNhap extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public GiaoDienDangNhap() {
        setTitle("Đăng nhập Từ điển");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblUser = new JLabel("Tài khoản:");
        JLabel lblPass = new JLabel("Mật khẩu:");

        txtUser = new JTextField();
        txtPass = new JPasswordField();
        btnLogin = new JButton("Đăng nhập");

        panel.add(lblUser); panel.add(txtUser);
        panel.add(lblPass); panel.add(txtPass);
        panel.add(new JLabel()); panel.add(btnLogin);

        add(panel);

        btnLogin.addActionListener(e -> xuLyDangNhap());
    }

    private void xuLyDangNhap() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        try {
            MayKhachTuDien client = new MayKhachTuDien();
            client.guiLenh("LOGIN " + user + " " + pass);
            String resp = client.nhanPhanHoi();

            if (resp.startsWith("LOGIN_OK")) {
                String role = resp.split(" ")[1];
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công (" + role + ")");
                dispose();
                new GiaoDienTuDien(client, role).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Không kết nối được tới server!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GiaoDienDangNhap().setVisible(true));
    }
}
