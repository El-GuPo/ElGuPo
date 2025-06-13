package com.ru.ami.hse.elgupo.scheduledEvents.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ru.ami.hse.elgupo.R;
import com.ru.ami.hse.elgupo.profile.photo.PhotoViewModel;
import com.ru.ami.hse.elgupo.profile.photo.Resource;
import com.ru.ami.hse.elgupo.serverrequests.tinderServices.models.User;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    private final PhotoViewModel photoViewModel;
    private final LifecycleOwner lifecycleOwner;
    private List<User> userList;

    public PersonAdapter(List<User> userList, PhotoViewModel photoViewModel, LifecycleOwner lifecycleOwner) {
        this.userList = userList;
        this.photoViewModel = photoViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_item_layout, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        User user = userList.get(position);
        Context context = holder.userPhoto.getContext();

        String sb = user.getName() + " " +
                user.getSurname();
        holder.userNameSurname.setText(sb);

        holder.userTg.setText(user.getTelegramTag());

        photoViewModel.getPhotoUrl(user.getId().longValue())
                .observe(lifecycleOwner, resource -> {
                    if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                        Glide.with(context)
                                .load(resource.data.toString())
                                .circleCrop()
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(holder.userPhoto);
                    } else if (resource.status == Resource.Status.ERROR) {
                        holder.userPhoto.setImageResource(R.drawable.user);
                    }
                });
    }

    public void updateUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {
        ImageView userPhoto;
        TextView userNameSurname;
        TextView userTg;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.userPhoto);
            userNameSurname = itemView.findViewById(R.id.userNameAndLastName);
            userTg = itemView.findViewById(R.id.userTg);
        }
    }
}
