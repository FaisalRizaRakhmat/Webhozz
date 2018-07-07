package wartaonline.chat.chatapp.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import wartaonline.chat.chatapp.R;
import wartaonline.chat.chatapp.models.Message;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.utils.PrefUtils;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.android.volley.VolleyLog.TAG;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Message> items; //tampung data
    private Context context;    //object activity

    //layout kiri-kanan pada chat
    private final static int ME_VIEW = 0;
    private final static int OTHER_VIEW = 1;

    public ChatAdapter(List<Message> items, Context context){
        this.items = items;
        this.context = context;
    }

    public void insertData(List<Message> data){
        this.items = data;
        notifyDataSetChanged();
    }

    //method menentukan layout yang digunakan
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutRes = 0;
        switch (viewType) {
            case ME_VIEW:
                layoutRes = R.layout.item_chat_me;
                break;
            case OTHER_VIEW:
                layoutRes = R.layout.item_chat_other;
                break;
        }
//        if(){
//
//        }
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = items.get(position);
        User userData = PrefUtils.getCurrentUser(this.context);
        Log.d(TAG, "getItemViewType ckckck: "+msg.tipe);
//        switch(msg.tipe) {
//            case 0:
//                return OTHER_VIEW;
//            default:
//                return ME_VIEW;
//        }
        if(msg.uid.equalsIgnoreCase(userData.uid) == true){
            return ME_VIEW;
        }else{
            return OTHER_VIEW;
        }
    }

    //method set data
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = items.get(position);
        holder.tvname.setText(message.name);
        holder.tvmessage.setText(message.message);
        holder.tvtanggal.setText(message.tanggal);
        holder.itemView.setTag(message);
        Picasso.with(context).load(message.avatar).into(holder.imageView);
    }

    //method ukuran recyclerview
    @Override
    public int getItemCount() {
        return items.size();
    }

    //agar tidak lag bila data banyak
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvname,  tvtanggal;
        private EmojiconTextView tvmessage;
        private ImageView imageView;

        public ViewHolder (View itemView){
            super(itemView);
            tvname = (TextView) itemView.findViewById(R.id.tvname);
            tvmessage = (EmojiconTextView) itemView.findViewById(R.id.tvmessage);
            tvtanggal = (TextView) itemView.findViewById(R.id.tvtanggal);
            imageView = (ImageView) itemView.findViewById(R.id.images);
        }
    }
}
