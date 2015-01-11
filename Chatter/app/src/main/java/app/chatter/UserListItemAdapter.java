package app.chatter;

import android.app.Activity;
import android.graphics.Paint;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

/**
 * Created by UDBHAV on 1/11/2015.
 */
public class UserListItemAdapter extends BaseAdapter
{
        private LayoutInflater layoutInflater;
        private  ArrayList<Pair<String, Message>> userItems=null;
        public UserListItemAdapter(Activity activity)
        {
            layoutInflater = activity.getLayoutInflater();
            userItems = new ArrayList<Pair<String, Message>>();
        }

        public void addItem( String recepient, Message message ) {
            userItems.add(new Pair(recepient, message));
            notifyDataSetChanged();
        }

        public void updateItem( String recepient, Message message ) {
            for(int i=0;i<userItems.size();i++)
                if(userItems.get(i).first==recepient)
                {
                    userItems.remove(i);
                    userItems.add(new Pair(recepient, message));
                }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return userItems.size();
        }

        @Override
        public Object getItem(int i) {
            return userItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            Message msg = userItems.get(i).second;

            //show message on left or right, depending on if
            //it's incoming or outgoing
            if (convertView == null) {
                int res = 0;
                res = R.layout.user_list_item;
                convertView = layoutInflater.inflate(res, viewGroup, false);
            }

            String recepient = userItems.get(i).first;

            TextView recepientTextview = (TextView) convertView.findViewById(R.id.usernameListItem);
            recepientTextview.setText(recepient);

            TextView msgTextview = (TextView) convertView.findViewById(R.id.msgListView);
            msgTextview.setText(msg.getBody());

            return convertView;

        }
}