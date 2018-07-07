package wartaonline.chat.chatapp.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import wartaonline.chat.chatapp.ChatGroupActivity;
import wartaonline.chat.chatapp.GroupDetailActivity;
import wartaonline.chat.chatapp.R;
import wartaonline.chat.chatapp.models.ChatGroup;

import static com.android.volley.VolleyLog.TAG;


public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private List<ChatGroup> items;
    private Context context;


    public RoomAdapter(Context activity, List<ChatGroup> items){
        this.context = activity;
        this.items = items;
    }


    public void insertData(List<ChatGroup> data){
        this.items = data;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatGroup item = items.get(position);
        holder.tvroomname.setText(item.roomname);
        holder.tvcreatedby.setText("Created by: " + item.username);
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvroomname, tvcreatedby;

        public ViewHolder(View itemView) {
            super(itemView);
            tvroomname = (TextView) itemView.findViewById(R.id.tvroomname);
            tvcreatedby = (TextView) itemView.findViewById(R.id.tvcreatedby);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatGroupActivity.class);
                    Log.d(TAG, "onClick: "+items.get(getAdapterPosition()).roomname);
                    intent.putExtra("room", items.get(getAdapterPosition()).roomname);
                    context.startActivity(intent);

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //longklik group details
                    Intent intent = new Intent(context, GroupDetailActivity.class);
                    //kirim data ke group details
                    intent.putExtra("room", items.get(getAdapterPosition()).roomname);
                    context.startActivity(intent);
                    return true;
                }
            });
        }
    }
}