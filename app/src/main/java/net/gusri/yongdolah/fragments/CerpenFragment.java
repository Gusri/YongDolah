package net.gusri.yongdolah.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.gusri.yongdolah.R;
import net.gusri.yongdolah.SingleActivity;

/**
 * Created by ghostonk on 22/12/16.
 */

public class CerpenFragment extends Fragment{
    private RecyclerView mBlogList;
    private Query mDatabase;
    private DatabaseReference mDBLike, mDBSmile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.view_blog, container, false);

        //Filter berdasarkan Kategory
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog").orderByChild("category").equalTo("Cerpen");
        mDatabase.keepSynced(true);

        mDBLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDBLike.keepSynced(true);

        mDBSmile = FirebaseDatabase.getInstance().getReference().child("Smiles");
        mDBSmile.keepSynced(true);

        mBlogList = (RecyclerView) mView.findViewById(R.id.rv_BlogList);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(lm);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<BlogModel, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<BlogModel, BlogViewHolder>(
                BlogModel.class,
                R.layout.view_list,
                BlogViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, BlogModel model, final int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setDateupload(model.getDateupload());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.setImguser(getActivity().getApplicationContext(), model.getImguser());
                viewHolder.setFirstname(model.getFirstname());
                viewHolder.setLastname(model.getLastname());

                viewHolder.mViewBlog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singBlogIntent = new Intent(getActivity().getApplicationContext(), SingleActivity.class);
                        singBlogIntent.putExtra("blog_id", post_key);
                        startActivity(singBlogIntent);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });

                //Hitung Jumlah children pada Firebase
                mDBLike.child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Log.e(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "");
                        viewHolder.setHitunglike(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDBSmile.child(post_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.setHitungSmile(dataSnapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mViewBlog;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mViewBlog = itemView;
        }

        public void setImage(final Context context, final String image) {
            final ImageView post_image = (ImageView) mViewBlog.findViewById(R.id.img_PostImage);
            Glide.with(context).load(image).placeholder(R.drawable.no_img).fitCenter().thumbnail(0.5f).crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(post_image);

        }

        public void setImguser(final Context ctx, final String imguser) {

            final ImageView post_imguser = (ImageView) mViewBlog.findViewById(R.id.img_PostUser);
            Glide.with(ctx).load(imguser).placeholder(R.drawable.noimages).centerCrop().thumbnail(0.5f).crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(post_imguser);
        }

        public void setHitunglike(String like) {
            TextView post_Hitung = (TextView) mViewBlog.findViewById(R.id.tv_countLikes);
            post_Hitung.setText(like);
        }

        public void setDesc(String desc) {
            TextView post_Desc = (TextView) mViewBlog.findViewById(R.id.tv_PostDesc);
            post_Desc.setText(desc);
        }

        public void setDateupload(String dateupload) {
            TextView post_dateupl = (TextView) mViewBlog.findViewById(R.id.tv_PostDate);
            post_dateupl.setText(dateupload);
        }

        public void setFirstname(String firstname) {
            TextView post_firstname = (TextView) mViewBlog.findViewById(R.id.tv_PostFirstname);
            post_firstname.setText(firstname);
        }

        //Text dari view_listt.xml
        public void setTitle(String title) {
            TextView post_Title = (TextView) mViewBlog.findViewById(R.id.tv_PostTitle);
            post_Title.setText(title);
        }

        public void setLastname(String lastname) {
            TextView post_lastname = (TextView) mViewBlog.findViewById(R.id.tv_PostLastname);
            post_lastname.setText(lastname);
        }

        public void setHitungSmile(String smile) {
            TextView mHitungSmile = (TextView) mViewBlog.findViewById(R.id.tv_countSmile);
            mHitungSmile.setText(smile);
        }
    }
}
