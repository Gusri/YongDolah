package net.gusri.yongdolah.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.gusri.yongdolah.R;

import java.util.List;

/**
 * Created by devandro on 5/3/17.
 */

public class UserListFragment extends Fragment {
    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";
    private RecyclerView mUserList;
    private DatabaseReference mDBUser;
    private Toolbar mToolbar;
    private List<BlogModel> mUser;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.item_user_list,container,false);

        mDBUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDBUser.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        mUserList = (RecyclerView) mView.findViewById(R.id.rv_UserList);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(lm);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<BlogModel, UserViewHolder> firebaseRecylerAdapter = new FirebaseRecyclerAdapter<BlogModel, UserViewHolder>
                (BlogModel.class, R.layout.item_user_view, UserViewHolder.class, mDBUser) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, BlogModel model, int position) {
                viewHolder.setImgUser(getActivity().getApplicationContext(), model.getImguser());
                viewHolder.setFirstname(model.getFirstname());
                viewHolder.setLastname(model.getLastname());
                viewHolder.setStatus(model.getStatus());

            }

        };
        mUserList.setAdapter(firebaseRecylerAdapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        View mViewUser;

        public UserViewHolder(View itemView) {
            super(itemView);
            mViewUser = itemView;
        }

        public void setImgUser(final Context ctx, final String imguser){
            final ImageView mImgUser = (ImageView) mViewUser.findViewById(R.id.img_User);
            Glide.with(ctx).load(imguser).placeholder(R.drawable.noimages).centerCrop().crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(mImgUser);

        }
        public void setFirstname(String firstname) {
            TextView mFirstname = (TextView) mViewUser.findViewById(R.id.tv_Firstname);
            mFirstname.setText(firstname);
        }

        public void setLastname(String lastname) {
            TextView mLastname = (TextView) mViewUser.findViewById(R.id.tv_Lastname);
            mLastname.setText(lastname);
        }

        public void setStatus(String status){
            TextView mStatus = (TextView) mViewUser.findViewById(R.id.tv_Status);
            mStatus.setText(status);
        }
    }


}
