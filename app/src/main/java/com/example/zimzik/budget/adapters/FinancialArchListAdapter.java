package com.example.zimzik.budget.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zimzik.budget.Helper;
import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Period;

import java.util.List;

public class FinancialArchListAdapter extends RecyclerView.Adapter<FinancialArchListAdapter.ViewHolder> {
    private List<Period> mPeriods;
    Context mContext;

    public FinancialArchListAdapter(List<Period> periods) {
        mPeriods = periods;
    }

    @Override
    public FinancialArchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.periods_list_item_view, parent,false);
        mContext = parent.getContext();
        return new FinancialArchListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Period period = mPeriods.get(position);
        holder.year.setText(String.valueOf(period.getYear()));
        holder.month.setText(Helper.getLocatedMonth(mContext)[period.getMonthNum()].toString());
        holder.money.setText(String.valueOf(period.getMoney()));
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
