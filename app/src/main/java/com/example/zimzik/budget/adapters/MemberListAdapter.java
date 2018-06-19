package com.example.zimzik.budget.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Member;
import com.example.zimzik.budget.fragments.MemberListFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> implements Filterable {
    private List<Member> mMemberList;
    private List<Member> mFilteredMembersList;
    private ClickAction<Member> mOnClickListener;
    private ClickAction<Member> mOnContextMenuClick;
    private Context mContext;
    private static final String DIRNAME = "avatarsDir";

    public MemberListAdapter(List<Member> membersList, ClickAction<Member> onClickListener, ClickAction<Member> onContextMenuClick) {
        this.mMemberList = membersList;
        this.mFilteredMembersList = membersList;
        this.mOnClickListener = onClickListener;
        this.mOnContextMenuClick = onContextMenuClick;
    }


    @Override
    public MemberListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberListAdapter.ViewHolder holder, int position) {
        final Member member = mFilteredMembersList.get(position);
        holder.name.setText(member.toString());
        holder.age.setText(MemberListFragment.calculateAge(member.getBirthday()) + " " + mContext.getString(R.string.yo));
        loadImageFromStorage(holder.avatar, member);
        holder.digit.setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(view.getContext(), holder.digit);
            menu.inflate(R.menu.member_list_context_menu);
            menu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.cmb_delete) {
                    mOnContextMenuClick.call(member);
                }
                return false;
            });
            menu.show();
        });
        holder.itemView.setOnClickListener(v -> mOnClickListener.call(member));
    }

    @Override
    public int getItemCount() {
        return mFilteredMembersList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    mFilteredMembersList = mMemberList;
                } else {
                    ArrayList<Member> filteredList = new ArrayList<>();
                    for(Member m: mMemberList) {
                        if(m.toString().toLowerCase().contains(charString)) {
                            filteredList.add(m);
                        }
                    }
                    mFilteredMembersList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredMembersList;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredMembersList = (List<Member>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final TextView name, age, digit;
        final ImageView avatar;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_name);
            age = view.findViewById(R.id.tv_age);
            digit = view.findViewById(R.id.tv_option_digit);
            avatar = view.findViewById(R.id.iv_avatar);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        }
    }

    private void loadImageFromStorage(ImageView imageView, Member member) {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir(DIRNAME, Context.MODE_PRIVATE);
        File file = new File(directory.getAbsolutePath(), member.getTimeIdent() + ".jpg");
        if (file.exists()) {
            imageView.setImageURI(Uri.fromFile(file));
        } else {
            imageView.setImageResource(R.drawable.ic_round_account_button_with_user_inside);
        }
    }


    public interface ClickAction<T> {
        void call(T object);
    }
}