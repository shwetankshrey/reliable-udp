import java.io.*;
import java.net.*;
import java.util.*;

public class Receiver {

    static DatagramSocket _sock;
    static InetAddress _ip;
    static int _window = 5;
    static int _windowpos = 0;

    public static void main(String args[]) throws Exception {

        _sock = new DatagramSocket(1805);
        _ip = InetAddress.getLocalHost();
        int[] window = new int[_window];
        int count = 0;

        HashMap<Integer, String> buffer = new HashMap<>();

        while (true) {
            byte[] _data = new byte[1024];
            DatagramPacket _rec = new DatagramPacket(_data, _data.length);
            _sock.receive(_rec);
            String _msg = new String(_rec.getData()).trim();
            
            System.out.println(_msg);
            count++;
            
            if (_msg.equals("DONE")) {
                break;
            }
            
            if (isConsistent(_msg)) {
                int s = Integer.parseInt(_msg.split(" ")[0].substring(2));
                if (_windowpos <= s && s < _windowpos + _window) {
                    window[s - _windowpos] = 1;
                }

                buffer.put(s, _msg);

                _msg = "S:" + _windowpos;
                _msg += " W:";
                for (int i = 0 ; i < _window ; i++) {
                    _msg += window[i];
                }
                _data = _msg.getBytes();
                DatagramPacket _ack = new DatagramPacket(_data, _data.length, _ip, 1705);
                _sock.send(_ack);

                while (true) {
                    if (window[0] == 1) {
                        for (int i = 0 ; i < _window-1 ; i++) {
                            window[i] = window[i+1];
                        }
                        window[_window-1] = 0;
                        _windowpos++;
                    }
                    else
                        break;
                }
            }
        }

        System.out.println("Received : " + count + " instead of 100");

        for (int i = 0 ; i < buffer.size() ; i++) {
            System.out.println(buffer.get(i));
        }

        _sock.close();
    }

    private static boolean isConsistent(String _msg) {
        try {
            byte[] _s = _msg.split(" ")[0].getBytes();
            String _c = _msg.split(" ")[1];
            int c = Integer.parseInt(_c.substring(2));
            int csm = 0;
            for (int j = 0 ; j < _s.length ; j++) csm += _s[j];
            if (csm == c) return true;
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }
}
