package TuDienApp;

import java.io.*;
import java.net.*;

public class MayKhachTuDien {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8888;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public MayKhachTuDien() throws IOException {
        socket = new Socket(SERVER_IP, SERVER_PORT);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void guiLenh(String lenh) {
        out.println(lenh);
    }

    public String nhanPhanHoi() throws IOException {
        return in.readLine();
    }

    public void dongKetNoi() throws IOException {
        socket.close();
    }
}
