package frc.robot;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClockAdapter extends Thread {

    private ServerSocket server;
    private volatile Socket connection = null;

    @Override
    public void run() {
		try {
			server = new ServerSocket(5808);
            connection = server.accept();
            sendTime(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void sendTime(int seconds) {
        try {
            if (connection == null) {
                System.err.println("WARNING: RahulClock has not connected to the robot yet.");
                server.close();
                return;
            }
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(seconds + "\n");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
