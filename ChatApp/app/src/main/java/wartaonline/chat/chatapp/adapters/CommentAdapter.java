package wartaonline.chat.chatapp.adapters;

/**
 * Created by faisalrizarakhmat on 27/01/18.
 */

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import wartaonline.chat.chatapp.R;
import wartaonline.chat.chatapp.models.Comment;
import wartaonline.chat.chatapp.models.User;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private RecyclerView recyclerView;
    private TimelineAdapter timelineAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private User userData;

    private StorageReference storageReference;

    private List<Comment> items;
    private List<String> itemkey;
    private Context context;

    private Uri mimageUri = null;

    public CommentAdapter(Context activity, List<Comment> items, List<String> itemkey) {
        this.context = activity;
        this.items = items;
        this.itemkey = itemkey;
    }

    public void insertData(List<Comment> data, List<String> datakey) {
        this.items = data;
        this.itemkey = datakey;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment item = items.get(position);
        //PostKey itemkey2 = itemkey.get(position);
        holder.txtkey.setText(this.itemkey.get(position));
        holder.username.setText(item.username);
        holder.content.setText(item.content);
    }

    @Override
    public int getItemCount() {
        return itemkey.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username, content, txtkey;

        public ViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            content = (TextView) itemView.findViewById(R.id.content);
            txtkey = (TextView) itemView.findViewById(R.id.txtkey);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, WorldDetailPostActivity.class);
//                    intent.putExtra("title", items.get(getAdapterPosition()).title);
//                    intent.putExtra("content", items.get(getAdapterPosition()).content);
//                    intent.putExtra("image", items.get(getAdapterPosition()).image);
//                    intent.putExtra("key", itemkey.get(getAdapterPosition()));
//                    context.startActivity(intent);
//
//
//                    TextView x = (TextView) view.findViewById(R.id.txtkey);
//                    String your_text = x.getText().toString();
//
//
//
//                    Log.d(TAG, "onClick: " + itemkey.get(getAdapterPosition()));
//                    //Log.d(TAG, "onClick: " + itemkey.get(getAdapterPosition()).key);
//                    Log.d(TAG, "clicked position2:" + items.get(getAdapterPosition()).title);
//                }
//            });

        }

    }
}

