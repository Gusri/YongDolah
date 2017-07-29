package net.gusri.yongdolah.chatgroup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by devandro on 5/4/17.
 */

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolderUsers> {
    @Override
    public ViewHolderUsers onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolderUsers holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolderUsers extends RecyclerView.ViewHolder{

        public ViewHolderUsers(View itemView) {
            super(itemView);
        }
    }

}
