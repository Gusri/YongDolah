package net.gusri.yongdolah.chatgroup;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.gusri.yongdolah.R;
import net.gusri.yongdolah.fragments.BlogModel;

/**
 * Created by devandro on 5/4/17.
 */

public class CGFragment extends Fragment {
    int SIGN_IN_REQUEST_CODE = 1;
    private DatabaseReference mDBChatUser, mDBMessage;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private RecyclerView mChatList;
    private FloatingActionButton fab;
    private EditText mInput;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.item_chat_group, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDBChatUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        mDBChatUser.keepSynced(true);

        mDBMessage = FirebaseDatabase.getInstance().getReference().child("chatGroup");
        mDBMessage.keepSynced(true);

        fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        mInput = (EditText) mView.findViewById(R.id.input);

        mChatList = (RecyclerView) mView.findViewById(R.id.rv_ChatList);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(lm);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiveMessage();
            }
        });

        return mView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void ReceiveMessage() {
        final String inputMessage_val = mInput.getText().toString().trim();
        final String ini_Date = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_UTC);
        final String ini_Time = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME);
        final DatabaseReference newMessage = mDBMessage.push();
        if (!TextUtils.isEmpty(inputMessage_val)) {
            mDBChatUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newMessage.child("date").setValue(ini_Date);
                    newMessage.child("time").setValue(ini_Time);
                    newMessage.child("imguser").setValue(dataSnapshot.child("imguser").getValue());
                    newMessage.child("firstname").setValue(dataSnapshot.child("firstname").getValue());
                    newMessage.child("lastname").setValue(dataSnapshot.child("lastname").getValue());
                    newMessage.child("message").setValue(inputMessage_val).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mInput.setText("");
                            mInput.setHint("Input Text");
                            mChatList.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mChatList.scrollToPosition(mChatList.getAdapter().getItemCount() - 1);
                                }
                            }, 1000);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<BlogModel, ChatViewHolder> firebaseMessageAdapter = new FirebaseRecyclerAdapter<BlogModel, ChatViewHolder>
                (BlogModel.class, R.layout.item_in_message, ChatViewHolder.class, mDBMessage) {
            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, BlogModel model, int position) {
                viewHolder.setImgUser(getActivity().getApplicationContext(), model.getImguser());
                viewHolder.setFirstname(model.getFirstname());
                viewHolder.setLastname(model.getLastname());
                viewHolder.setMessage(model.getMessage());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
            }
        };

        mChatList.setAdapter(firebaseMessageAdapter);

        firebaseMessageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mChatList.smoothScrollToPosition(firebaseMessageAdapter.getItemCount() - 1);
            }
        });
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        View mViewChat;

        public ChatViewHolder(View itemView) {
            super(itemView);
            mViewChat = itemView;
        }

        public void setImgUser(final Context ctx, final String imguser){
            final ImageView mImgUserName = (ImageView) mViewChat.findViewById(R.id.img_UserName);
            Glide.with(ctx).load(imguser).placeholder(R.drawable.noimages).centerCrop().crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(mImgUserName);

        }

        public void setFirstname(String firstname) {
            TextView mFirstname = (TextView) mViewChat.findViewById(R.id.tv_Firstuser);
            mFirstname.setText(firstname);
        }

        public void setLastname(String lastname) {
            TextView mLastname = (TextView) mViewChat.findViewById(R.id.tv_Lastuser);
            mLastname.setText(lastname);
        }

        public void setMessage(String message) {
            TextView mMessage = (TextView) mViewChat.findViewById(R.id.message_text);
            mMessage.setText(message);
        }

        public void setDate(String date) {
            TextView mDate = (TextView) mViewChat.findViewById(R.id.message_date);
            mDate.setText(date);
        }

        public void setTime(String time) {
            TextView mTime = (TextView) mViewChat.findViewById(R.id.message_time);
            mTime.setText(time);
        }

    }


}
