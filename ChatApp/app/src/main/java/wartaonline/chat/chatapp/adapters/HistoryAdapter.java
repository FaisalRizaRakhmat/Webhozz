package wartaonline.chat.chatapp.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wartaonline.chat.chatapp.ChatActivity;
import wartaonline.chat.chatapp.R;
import wartaonline.chat.chatapp.models.Message;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.utils.PrefUtils;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<Message> items;
    private Context context;
    private User userData;

    public HistoryAdapter(Context activity, List<Message> items){
        this.context = activity;
        this.items = items;
    }


    public void insertData(List<Message> data){
        Collections.sort(data, new Comparator<Message>(){
            public int compare(Message obj1, Message obj2)
            {
                if (obj1.getTanggal() == null || obj2.getTanggal() == null)
                    return 0;
                //return obj1.tanggal > obj2.tanggal ? 1 : (obj1.tanggal < obj2.tanggal ? -1 : 0);

                return obj1.getTanggal().compareTo(obj2.getTanggal());
            }
        });
        this.items = data;
        Collections.reverse(this.items);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message item = items.get(position);
        userData = PrefUtils.getCurrentUser(this.context);
        holder.tvroomname.setText(item.name);
        holder.tvcreatedby.setText(item.message);
        holder.datemessage.setText(item.tanggal);

        //if(item.name.equalsIgnoreCase(item.originalname) == false){
        if (item.originalname.equalsIgnoreCase(userData.name) == false){
            holder.newchat.setText(" New ");
            holder.newchat.setVisibility(View.VISIBLE);
            //holder.newchat.setVisibility(View.GONE);
        }else{
            holder.newchat.setVisibility(View.GONE);
            //holder.newchat.setText(item.originalname+" New "+userData.name);
        }
        //holder.newchat.setText(item.originalname);
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvroomname, tvcreatedby, newchat, datemessage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvroomname = (TextView) itemView.findViewById(R.id.tvroomname);
            tvcreatedby = (TextView) itemView.findViewById(R.id.tvcreatedby);
            newchat = (TextView) itemView.findViewById(R.id.newchat);
            datemessage = (TextView) itemView.findViewById(R.id.datemessage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("uid", items.get(getAdapterPosition()).uid);
                    intent.putExtra("name", items.get(getAdapterPosition()).name);
                    context.startActivity(intent);

                }
            });

        }
    }
}