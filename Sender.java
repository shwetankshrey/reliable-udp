import java.io.*;
import java.net.*;
import java.util.*;

public class Sender {

    static DatagramSocket _sock;
    static InetAddress _ip;
    static DatagramPacket[] _pkt;
    static boolean[] acked;
    static int _window = 10;

    public static void main(String args[]) throws Exception {

        _sock = new DatagramSocket(1705);
        _ip = InetAddress.getLocalHost();
        _pkt = new DatagramPacket[101];
        acked = new boolean[101];

        for (int i = 0 ; i < 100 ; i++) {
            byte[] _data = new byte[1024];
            String _msg = "S:" + i;
            _data = _msg.getBytes();
            int csm = 0;
            for (int j = 0 ; j < _data.length ; j++) {
                csm += _data[j];
            }
            _msg += " C:" + csm;
            _data = _msg.getBytes();
            _pkt[i] = new DatagramPacket(_data, _data.length, _ip, 1805);
        }
        String _dones = "DONE";
        byte[] _doneb = _dones.getBytes();
        _pkt[100] = new DatagramPacket(_doneb, _doneb.length, _ip, 1805);

        startSending();

        _sock.close();
    }

    private static void startSending() throws Exception {
        
        for (int i = 0 ; i < _window ; i++) {
            sendPkt(i);
        }
        int _windowpos = 0;
        while (true) {
            byte[] _data = new byte[1024];
            DatagramPacket _rec = new DatagramPacket(_data, _data.length);
            _sock.receive(_rec);

            String _msg = new String(_rec.getData()).trim();

            System.out.println(_msg);

            try {
                int _recpos = Integer.parseInt(_msg.split(" ")[0].substring(2));
                String _recwin = _msg.split(" ")[1].substring(2);
                for (int i = 0 ; i < _recpos ; i++) {
                    acked[i] = true;
                }
                for (int i = 0 ; i < _window ; i++) {
                    if (_recwin.charAt(i) == '1') {
                        acked[i + _recpos] = true;
                    }
                }
            }
            catch (Exception e) {}
            int w = 0;
            while (true) {
                if (acked[_windowpos] == true && _windowpos < 100 - _window) {
                    _windowpos++;
                    w++;
                }
                else
                    break;
            }

            for (int i = w ; i > 0 ; i--) {
                sendPkt(_windowpos + _window - i);
            }

            boolean end = true;
            for (int i = 0 ; i < 100 ; i++) {
                end &= acked[i];
            }

            if (end) {
                while (true) {
                    System.out.println("Sending finished.");
                    sendPkt(100);
                }
            }
        }
    }

    private static void sendPkt(int i) throws Exception {
        System.out.println("Sending " + i + " out of 100.");
        _sock.send(_pkt[i]);
        TimerTask _timeout = new TimerTask() {
            public void run() {
                try {
                    if (!acked[i]) 
                        sendPkt(i);
                }
                catch (Exception e) {}
            }
        };
        Timer timer = new Timer();
        timer.schedule(_timeout, 1000L);
    }
}
