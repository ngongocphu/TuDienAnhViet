package TuDienApp;

import java.io.*;
import java.net.*;
import java.util.*;

public class XuLyKhach extends Thread {
    private Socket socket;
    private Map<String, String> tuDien;
    private Map<String, String> taiKhoan;
    private String fileTuDien;
    private String username;
    private boolean isAdmin;

    private BufferedReader in;
    private PrintWriter out;

    public XuLyKhach(Socket socket, Map<String, String> tuDien, Map<String, String> taiKhoan, String fileTuDien) {
        this.socket = socket;
        this.tuDien = tuDien;
        this.taiKhoan = taiKhoan;
        this.fileTuDien = fileTuDien;
    }

    @Override
    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Đăng nhập
            while (true) {
                String login = in.readLine();
                if (login == null) break;
                String[] parts = login.split(" ");
                if (parts.length == 3 && parts[0].equalsIgnoreCase("LOGIN")) {
                    String user = parts[1];
                    String pass = parts[2];
                    if (taiKhoan.containsKey(user) && taiKhoan.get(user).equals(pass)) {
                        username = user;
                        isAdmin = user.equals("admin");
                        out.println("LOGIN_OK " + (isAdmin ? "admin" : "user"));
                        break;
                    } else {
                        out.println("LOGIN_FAIL");
                    }
                }
            }

            // Xử lý lệnh
            String line;
            while ((line = in.readLine()) != null) {
                String[] cmd = line.split(" ", 2);
                String action = cmd[0].toUpperCase();

                switch (action) {
                    case "SEARCH": // Tra cứu
                        xuLySearch(cmd[1]);
                        break;
                    case "ADD": // Thêm từ
                        if (isAdmin) xuLyAdd(cmd[1]); else out.println("❌ Chỉ admin mới được thêm từ!");
                        break;
                    case "DELETE": // Xóa từ
                        if (isAdmin) xuLyDelete(cmd[1]); else out.println("❌ Chỉ admin mới được xóa từ!");
                        break;
                    case "UPDATE": // Cập nhật nghĩa
                        if (isAdmin) xuLyUpdate(cmd[1]); else out.println("❌ Chỉ admin mới được sửa nghĩa!");
                        break;
                    case "SHOWALL": // Hiện tất cả từ
                        xuLyShowAll();
                        break;
                    case "FUZZY": // Tìm gần đúng
                        xuLyFuzzy(cmd[1]);
                        break;
                    case "EXIT": // Thoát
                        out.println("BYE");
                        socket.close();
                        return;
                    default:
                        out.println("❌ Lệnh không hợp lệ!");
                }
            }

        } catch (IOException e) {
            System.out.println("⚠ Lỗi kết nối client: " + e.getMessage());
        }
    }

    private void xuLySearch(String tu) {
        tu = tu.toLowerCase();
        if (tuDien.containsKey(tu)) {
            out.println("✅ " + tu + " = " + tuDien.get(tu));
        } else {
            out.println("❌ Không tìm thấy từ: " + tu);
        }
    }

    private void xuLyAdd(String data) {
        String[] parts = data.split("=", 2);
        if (parts.length == 2) {
            tuDien.put(parts[0].toLowerCase(), parts[1]);
            luuTuDien();
            out.println("✅ Đã thêm: " + data);
        } else {
            out.println("❌ Cú pháp sai! ADD tu=nghia");
        }
    }

    private void xuLyDelete(String tu) {
        tu = tu.toLowerCase();
        if (tuDien.remove(tu) != null) {
            luuTuDien();
            out.println("✅ Đã xóa: " + tu);
        } else {
            out.println("❌ Không tìm thấy từ: " + tu);
        }
    }

    private void xuLyUpdate(String data) {
        String[] parts = data.split("=", 2);
        if (parts.length == 2 && tuDien.containsKey(parts[0].toLowerCase())) {
            tuDien.put(parts[0].toLowerCase(), parts[1]);
            luuTuDien();
            out.println("✅ Đã cập nhật: " + data);
        } else {
            out.println("❌ Không tìm thấy từ để sửa!");
        }
    }

    private void xuLyShowAll() {
        if (tuDien.isEmpty()) {
            out.println("❌ Từ điển trống!");
        } else {
            StringBuilder sb = new StringBuilder("📖 Danh sách từ:\n");
            for (Map.Entry<String, String> e : tuDien.entrySet()) {
                sb.append(e.getKey()).append(" = ").append(e.getValue()).append("\n");
            }
            out.println(sb.toString());
        }
    }

    private void xuLyFuzzy(String tu) {
        tu = tu.toLowerCase();
        List<String> ketQua = new ArrayList<>();
        for (String word : tuDien.keySet()) {
            if (levenshtein(tu, word) <= 2) {
                ketQua.add(word);
            }
        }
        if (ketQua.isEmpty()) {
            out.println("❌ Không tìm thấy từ gần đúng!");
        } else {
            out.println("🔎 Gợi ý: " + String.join(", ", ketQua));
        }
    }

    private void luuTuDien() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileTuDien))) {
            for (Map.Entry<String, String> e : tuDien.entrySet()) {
                pw.println(e.getKey() + "=" + e.getValue());
            }
        } catch (IOException e) {
            System.out.println("⚠ Lỗi khi lưu từ điển!");
        }
    }

    // Tính khoảng cách Levenshtein
    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }
}
