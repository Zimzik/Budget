package com.example.zimzik.budget.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.zimzik.budget.Helper;
import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Period;

import java.util.List;

public class FinancialListAdapter extends RecyclerView.Adapter<FinancialListAdapter.ViewHolder> {
    private List<Period> mPeriods;
    private LongClick<Period> mLongClickListener;
    Context mContext;

    public FinancialListAdapter(List<Period> periods, LongClick<Period> longClick) {
        mPeriods = periods;
        mLongClickListener = longClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.periods_list_item_view, parent,false);
        mContext = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FinancialListAdapter.ViewHolder holder, int position) {
        final Period period = mPeriods.get(position);
        holder.year.setText(String.valueOf(period.getYear()));
        //holder.month.setText(Months.values()[period.getMonthNum()].toString());
        holder.month.setText(Helper.getLocatedMonth(mContext)[period.getMonthNum()].toString());
        holder.money.setText(String.valueOf(period.getMoney()));
        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(v.getContext(), holder.itemView);
            menu.inflate(R.menu.period_list_context_menu);
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.cp_delete) {
                    mLongClickListener.call(period);
                }
                return true;
            });
            menu.show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mPeriods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView year, month, money;

        ViewHolder(View view) {
            super(view);
            year = view.findViewById(R.id.tv_period_year);
            month = view.findViewById(R.id.tv_period_month);
            money = view.findViewById(R.id.tv_period_money);
        }
    }

    public interface LongClick<T> {
        void call(T object);
    }
}