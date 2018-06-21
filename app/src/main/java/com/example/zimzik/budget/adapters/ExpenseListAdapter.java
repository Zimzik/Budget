package com.example.zimzik.budget.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.zimzik.budget.R;
import com.example.zimzik.budget.data.db.models.Expense;
import com.example.zimzik.budget.data.db.models.Income;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExpenseListAdapter  extends RecyclerView.Adapter<ExpenseListAdapter.ViewHolder> {

    private List<Expense> mExpenses;
    private FinancialListAdapter.LongClick<Expense> mLongClick;

    public ExpenseListAdapter(List<Expense> expenses, FinancialListAdapter.LongClick<Expense> longClick) {
        mExpenses = expenses;
        mLongClick = longClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_item_view, parent, false);
        return new ExpenseListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Expense expense = mExpenses.get(position);
        holder.date.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(expense.getDate())));
        holder.description.setText(expense.getDescription());
        holder.money.setText(String.valueOf(expense.getSumm()));
        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(v.getContext(), holder.itemView);
            menu.inflate(R.menu.period_list_context_menu);
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.cp_delete) {
                    mLongClick.call(expense);
                }
                return true;
            });
            menu.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView date, description, money;

        ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.tv_expense_date);
            description = view.findViewById(R.id.tv_expense_description);
            money = view.findViewById(R.id.tv_expense_money);
        }
    }
}
