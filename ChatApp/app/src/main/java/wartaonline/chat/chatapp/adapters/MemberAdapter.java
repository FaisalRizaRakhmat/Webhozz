package wartaonline.chat.chatapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import wartaonline.chat.chatapp.ChatActivity;
import wartaonline.chat.chatapp.R;
import wartaonline.chat.chatapp.models.User;
import wartaonline.chat.chatapp.utils.PrefUtils;

import static android.content.ContentValues.TAG;


public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private List<User> items;
    private Context context;
    private DatabaseReference mref;
    public Boolean isCreator;
    public Boolean isLogin;
    private List<String> itemstatus;

    public MemberAdapter(List<User> users, Context context,DatabaseReference mref, Boolean isCreator){
        this.items = users;
        this.context = context;
        this.mref = mref;
        this.isCreator = isCreator;
    }

    public void insertData(List<User> data, List<String> status){
        this.items = data;
        this.itemstatus = status;
        notifyDataSetChanged();
    }


    //tempelin data
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    //set data
    @Override
    public void onBindViewHolder(MemberAdapter.ViewHolder holder, int position) {
        User user = items.get(position);
        holder.tvusername.setText(user.name);
        if(this.itemstatus.get(position) == "false"){
            holder.itemstatusx.setText("New");
        }else{
            holder.itemstatusx.setText("");
        }

        User usercheck = PrefUtils.getCurrentUser(context);
        //if(usercheck.uid.equals(user.uid)) {
        if(user.uid.equals(usercheck.uid)){
            //if(this.itemstatus.get(position).equals("true")){
                isLogin = Boolean.parseBoolean(this.itemstatus.get(position));

            //}
            //if(this.itemstatus.get(position).equals("false")){
            //    this.isLogin = false;
            //}

            //this.isLogin = this.itemstatus.get(position);
        }
        //user..name.toString()

        //if(user.name.toString()){
        //    this.isLogin =
        //}
        //holder.itemstatus.setText(this.itemstatus.get(position));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvusername, itemstatusx;


        public ViewHolder(View itemView) {
            super(itemView);

            tvusername = (TextView) itemView.findViewById(R.id.tvusername);
            itemstatusx = (TextView) itemView.findViewById(R.id.status);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "ViewHolder: data "+itemstatusx.getText());
                        //Log.d(TAG, "onBindViewHolder2: "+this.isLogin);
                        showActionDialog(items.get(getAdapterPosition()).uid,items.get(getAdapterPosition()).name, itemstatusx.getText().toString());
                    }
                });


        }
    }

    public void showActionDialog(final String uid, final String name, final String status){

        //validasi action member
        User user = PrefUtils.getCurrentUser(context);

        List<String> action = new ArrayList<>();
//        if(!user.uid.equals(uid)){
//            //si pembuat grup bisa accept dan remove tetapi di luar itu hanya chat
//            //if(isCreator){
//                if(status.equals("New")) {
//                    action.add("Accept");
//                }
//                action.add("Remove");
//            //}
//            action.add("Chat");
//        }

        if(isLogin.booleanValue() == true && !user.uid.equals(uid)){
            //action.add("Chat");
            if(status.equals("New")) {
                action.add("Accept");
            }
            action.add("Remove");
        }


        final CharSequence[] arrays = action.toArray(new String[action.size()]);
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(context);
        dialogbuilder.setTitle("Action : ");

        dialogbuilder.setItems(arrays, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedText = arrays[which].toString();
                // jika di klik accept
                if(selectedText.equals("Accept")){
                    mref.child(uid).setValue(true);
                }
                if (selectedText.equals("Remove")){
                    mref.child(uid).removeValue();
                }

                if (selectedText.equals("Chat")){

                    // jika di klik chat
                    Intent intent = new Intent(context, ChatActivity.class);
                    // kirim data
                    intent.putExtra("uid",uid);
                    intent.putExtra("name",name);
                    context.startActivity(intent);
                }

            }
        });

        AlertDialog alertDialog = dialogbuilder.create();
        alertDialog.show();
    }

}
