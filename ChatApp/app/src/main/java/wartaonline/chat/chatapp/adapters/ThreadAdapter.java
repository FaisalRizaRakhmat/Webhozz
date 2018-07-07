package wartaonline.chat.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import wartaonline.chat.chatapp.CommentThreadActivity;
import wartaonline.chat.chatapp.R;
import wartaonline.chat.chatapp.models.Post;
import wartaonline.chat.chatapp.models.User;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

    private RecyclerView recyclerView;
    private ThreadAdapter timelineAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private User userData;

    private StorageReference storageReference;

    private List<Post> items;
    private List<String> itemkey;
    private Context context;

    private Uri mimageUri = null;

    public ThreadAdapter(Context activity, List<Post> items, List<String> itemkey){
        this.context = activity;
        this.items = items;
        this.itemkey = itemkey;
    }

    public void insertData(List<Post> data, List<String> datakey){
        this.items = data;
        this.itemkey = datakey;
        notifyDataSetChanged();
    }


    @Override
    public ThreadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_room2, parent, false);
        return new ThreadAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ThreadAdapter.ViewHolder holder, int position) {
        Post item = items.get(position);
        holder.txtkey.setText(this.itemkey.get(position));
        holder.tvroomname.setText(item.title);
        holder.tvcreatedby.setText(item.content);

        Picasso.with(context).load(item.image).into(holder.imgview);
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return itemkey.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvroomname, tvcreatedby, txtkey;
        private ImageView imgview;

        public ViewHolder(View itemView) {
            super(itemView);
            tvroomname = (TextView) itemView.findViewById(R.id.tvroomname);
            tvcreatedby = (TextView) itemView.findViewById(R.id.tvcreatedby);
            txtkey = (TextView) itemView.findViewById(R.id.txtkey);
            imgview = (ImageView) itemView.findViewById(R.id.imgview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CommentThreadActivity.class);
                    intent.putExtra("title", items.get(getAdapterPosition()).title);
                    intent.putExtra("content", items.get(getAdapterPosition()).content);
                   intent.putExtra("image", items.get(getAdapterPosition()).image);
                    intent.putExtra("key", itemkey.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

        }

    }

}