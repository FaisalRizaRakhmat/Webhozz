package wartaonline.chat.chatapp.adapters;

import android.content.Context;
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

import wartaonline.chat.chatapp.R;
import wartaonline.chat.chatapp.models.Items;
import wartaonline.chat.chatapp.models.User;

/**
 * Created by Alfian on 1/11/2018.
 */

public class ItemAdapter extends RecyclerView.Adapter <ItemAdapter.ViewHolder> {

    private RecyclerView recyclerView;
    private TimelineAdapter timelineAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private User userData;

    private StorageReference storageReference;

    private List<Items> items;
    private Context context;

    private Uri mimageUri = null;

    public ItemAdapter(Context activity, List<Items> items){
        this.context = activity;
        this.items = items;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.barang_room, parent, false);
        return new ViewHolder(v);
    }

    public void insertData(List<Items> data){
        this.items = data;
        notifyDataSetChanged();
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Items item = items.get(position);
        holder.nama.setText(item.nama);
        holder.pembuat.setText(item.pembuat);
        holder.harga.setText(item.harga);
        Picasso.with(context).load(item.image).into(holder.imgview);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nama, pembuat,harga;
        private ImageView imgview;

        public ViewHolder(View itemView) {
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.tvroomname);
            pembuat = (TextView) itemView.findViewById(R.id.tvcreatedby);
            harga = (TextView) itemView.findViewById(R.id.tvcreatedby);
            imgview = (ImageView) itemView.findViewById(R.id.imgview);


    }
    }
}
