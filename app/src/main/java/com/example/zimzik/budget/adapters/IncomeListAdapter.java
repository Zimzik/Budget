package com.example.zimzik.budget.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Income;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IncomeListAdapter extends RecyclerView.Adapter<IncomeListAdapter.ViewHolder> {

    private List<Income> mIncomes;
    private FinancialListAdapter.LongClick<Income> mLongClick;

    public IncomeListAdapter(List<Income> incomes, FinancialListAdapter.LongClick<Income> longClick) {
        mIncomes = incomes;
        mLongClick = longClick;
    }

    @NonNull
    @Override
    public IncomeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_list_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Income income = mIncomes.get(position);
        holder.date.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(income.getDate())));
        holder.description.setText(income.getDescription());
        holder.money.setText(String.valueOf(income.getSumm()));
        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(v.getContext(), holder.itemView);
            menu.inflate(R.menu.period_list_context_menu);
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.cp_delete) {
                    mLongClick.call(income);
                }
                return true;
            });
            menu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mIncomes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView date, description, money;

        ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.tv_income_date);
            description = view.findViewById(R.id.tv_income_description);
            money = view.findViewById(R.id.tv_income_money);
        }
    }
}
