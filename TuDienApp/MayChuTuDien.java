package TuDienApp;

import java.io.*;
import java.net.*;
import java.util.*;

public class MayChuTuDien {
    private static final int PORT = 8888;
    protected static Map<String, String> tuDien = new HashMap<>();
    protected static Map<String, String> taiKhoan = new HashMap<>();
    private static final String FILE_TU_DIEN = "tudien.txt";

    public static void main(String[] args) {
        napTaiKhoan();
        napTuDien();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("📌 Đã nạp tài khoản: " + taiKhoan.size());
            System.out.println("📌 Đã nạp từ điển: " + tuDien.size() + " từ");
            System.out.println("📌 Server từ điển đang chạy ở cổng " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new XuLyKhach(clientSocket, tuDien, taiKhoan, FILE_TU_DIEN).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void napTaiKhoan() {
        taiKhoan.put("admin", "123"); // admin có quyền thêm/xóa/sửa
        taiKhoan.put("user", "123");  // user chỉ có quyền tra cứu
    }

    private static void napTuDien() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_TU_DIEN))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    tuDien.put(parts[0].trim().toLowerCase(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("⚠ Không tìm thấy file " + FILE_TU_DIEN + ", tạo từ điển rỗng.");
        }
    }
}
