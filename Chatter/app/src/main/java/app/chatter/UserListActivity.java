package app.chatter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserListActivity extends ActionBarActivity {

    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private ArrayList<Chat> chats;
//    private Map<String,ArrayList<Message>> db;
    private ListView usersListView;
    private UserListItemAdapter listItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        chats = new ArrayList<>();
//        db = new HashMap<>();
        names = new ArrayList<>();

        TextView tv = (TextView) this.findViewById(R.id.txtMessageView);
        usersListView = (ListView) this.findViewById(R.id.userList);

        listItemAdapter=new UserListItemAdapter( this );
        usersListView.setAdapter(listItemAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                openConversation(i);
            }
        });

        ChatManager cm = ChatManager.getInstanceFor(Global.connection);
        cm.addChatListener(new MyChatManagerListener(this));
    }

    public void openConversation(int pos) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        Global.chat = chats.get(pos);
        Global.recepientid = Global.chat.getParticipant();
//        Global.db = db;
        startActivity(intent);

    }

    private class MyChatManagerListener implements ChatManagerListener {
        UserListActivity parent = null;

        MyChatManagerListener(UserListActivity parent) {
            this.parent = parent;
        }

        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            if (!createdLocally) chat.addMessageListener(new MyChatMessageListener(parent));
            updateVariables(chat);
            parent.names.add(chat.getParticipant());
            listItemAdapter.addItem(chat.getParticipant(), new Message());
        }

        private void updateVariables(Chat chat) {
            chats.add(chat);
        }
    }

    private class MyChatMessageListener implements ChatMessageListener {
        UserListActivity parent = null;

        MyChatMessageListener(UserListActivity parent) {
            this.parent = parent;
        }

        @Override
        public void processMessage(Chat chat, Message message) {
            String name = chat.getParticipant();
//            if (parent.db.get(name) == null)
//                parent.db.put(chat.getParticipant(), new ArrayList<Message>());
//            parent.db.get(name).add(message);

            try {
                if (message.getBody() == null || message.getBody().equalsIgnoreCase("")
                        || message.getBody().equalsIgnoreCase("\n")) return;

                Global.database.execSQL("INSERT INTO chat VALUES('" + name + "','" + message.getBody() + "',datetime())");
            } catch (Exception e){
                e.printStackTrace();
            }

            String msg = name + " --> " + message.getBody();
            Log.showLog(msg);
            parent.updateListView( chat.getParticipant(), message);
        }
    }

    private void updateListView( final String name, final Message message) {
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                /*namesArrayAdapter =
                        new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.user_list_item, names);
                usersListView.setAdapter(namesArrayAdapter);*/
                listItemAdapter.updateItem(name, message);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
