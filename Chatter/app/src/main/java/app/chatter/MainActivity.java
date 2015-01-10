package app.chatter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//User defined imports
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ChatMessageListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

import javax.net.ssl.SSLSocketFactory;


public class MainActivity extends ActionBarActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)this.findViewById(R.id.buttonLogin);
        final EditText username = (EditText)this.findViewById(R.id.txtUsername);
        final EditText password = (EditText)this.findViewById(R.id.txtPassword);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.showLog("Username - "+username.getText()+" Password - "+password.getText());
                final String usr = username.getText().toString();
                final String pwd = password.getText().toString();

                new ConnectionTask(usr,pwd).execute(usr,pwd);
                
            }
        });
    }

    private void showToast(){
        Context context = getApplicationContext();
        CharSequence text = "Invalid Credentials!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private XMPPTCPConnection getConnection(String username,String password) {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username,password)
                .setServiceName(Constants.DOMAIN) //TODO: ADD AS CONSTANTS
                .setHost(Constants.HOST)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.enabled)
                .setSocketFactory(SSLSocketFactory.getDefault())
                .setPort(Constants.PORT)
                .build();

        return new XMPPTCPConnection(config);
    }

    private class ConnectionTask extends AsyncTask< String, Void, Void > {

        private String usr;
        private  String pwd;

        ConnectionTask(String usr, String pwd)
        {
            this.usr=usr;
            this.pwd=pwd;
        }

        @Override
        protected Void doInBackground(String... param) {

            Global.connection = getConnection(usr,pwd);
            Log.showLog("usr:"+usr+" pwd:"+pwd+" doInBackground() exiting");

            try {
                Global.connection.connect();
                Global.connection.login();
            } catch (SmackException e) {
                e.printStackTrace();
                showToast();
            } catch (IOException e) {
                e.printStackTrace();
                showToast();
            } catch (XMPPException e) {
                e.printStackTrace();
                showToast();
            }
            if (Global.connection.isAuthenticated()) {
                ChatManager cm = ChatManager.getInstanceFor(Global.connection);
                cm.addChatListener(new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        if(!createdLocally) chat.addMessageListener(new ChatMessageListener() {
                            @Override
                            public void processMessage(Chat chat, Message message) {
                                String name = chat.getParticipant();
                                String msg = name+" --> "+message.getBody();
                                System.out.println(msg);

                            }
                        });
                    }
                });
                Intent intent = new Intent("android.intent.action.USERLIST");
                startActivity(intent);
            }else {
                showToast();
            }

            System.out.println(Global.connection.isAuthenticated());

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {

            Log.showLog("onPostExecute()");
        }
    }
}
