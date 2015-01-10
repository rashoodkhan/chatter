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
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
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
                System.out.println("Username - "+username.getText()+" Password - "+password.getText());
                final String usr = username.getText().toString();
                final String pwd = password.getText().toString();

                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Global.connection = getConnection(usr,pwd);
                        try {
                            Global.connection.connect();
                            Global.connection.login();
                        } catch (SmackException e) {
                            e.printStackTrace();
//                            showToast();
                        } catch (IOException e) {
                            e.printStackTrace();
//                            showToast();
                        } catch (XMPPException e) {
                            e.printStackTrace();
//                            showToast();
                        }

                        if (Global.connection.isAuthenticated()) {
                            Intent intent = new Intent("android.intent.action.USERLIST");
                            startActivity(intent);
                        }else {
                            showToast();
                        }

                        System.out.println(Global.connection.isAuthenticated());
                    }
                });
                //th.start();
                new ConnectionTask().execute(usr,pwd);

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
                .setServiceName("xmpp.yellowmssngr.com") //TODO: ADD AS CONSTANTS
                .setHost("xmpp.yellowmssngr.com")
                .setSecurityMode(ConnectionConfiguration.SecurityMode.enabled)
                .setSocketFactory(SSLSocketFactory.getDefault())
                .setPort(443)
                .build();

        return new XMPPTCPConnection(config);
    }

    private class ConnectionTask extends AsyncTask< String, Void, Void > {
        protected Void doInBackground(String... userDetails) {
            int count = userDetails.length;
            String usr=userDetails[0];
            String pwd=userDetails[1];

            Global.connection = getConnection(usr,pwd);

            return null;
        }

        protected void onPostExecute() {

            if (Global.connection.isAuthenticated()) {
                Intent intent = new Intent("android.intent.action.USERLIST");
                startActivity(intent);
            }else {
                showToast();
            }

            System.out.println(Global.connection.isAuthenticated());
        }
    }
}
