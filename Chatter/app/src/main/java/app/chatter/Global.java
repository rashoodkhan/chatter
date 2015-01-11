package app.chatter;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by rashid on 1/10/15.
 */
public class Global {

    public static XMPPTCPConnection connection = null;
    public static String userid = null;
    public static String recepientid = null;
    public static Chat chat = null;
    public static Map<String,ArrayList<Message>> db;
}
