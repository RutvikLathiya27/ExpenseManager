package com.example.expensemanager;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IncomeViewHolder extends RecyclerView.ViewHolder {

    public RecyclerView root;
    public TextView txtType;
    public TextView txtDesc;
    public TextView txtDate;
    public TextView txtAmmount;

    public IncomeViewHolder(@NonNull View itemView) {
        super(itemView);

        root = itemView.findViewById(R.id.recycler_id_income);
        txtType = itemView.findViewById(R.id.type_txt_income);
        txtDesc = itemView.findViewById(R.id.note_txt_income);
        txtDate = itemView.findViewById(R.id.date_txt_income);
        txtAmmount = itemView.findViewById(R.id.ammount_txt_income);


    }

    public void setTxtType(String string){
        txtType.setText(string);
    }
    public void setTxtDesc(String string){
        txtDesc.setText(string);
    }
    public void setTxtDate(String string){
        txtDate.setText(string);
    }
    public void setTxtAmmount(int string){
        txtAmmount.setText(String.valueOf(string));
    }
}
