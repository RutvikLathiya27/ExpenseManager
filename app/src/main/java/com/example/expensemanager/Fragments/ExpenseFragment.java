package com.example.expensemanager.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.expensemanager.ExpenseViewHolder;
import com.example.expensemanager.IncomeViewHolder;
import com.example.expensemanager.Model.Data;
import com.example.expensemanager.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {

    //FirebaseDatabae...

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //RecyclerView..

    private RecyclerView recyclerView;

    private FirebaseRecyclerAdapter adapter;

    private TextView expenseTotalSum;

    //Update edit Text

    private EditText edtAmmount, edtType, edtNote;
    private Button btnUpdate, btnDelete;

    //Data item value

    private String type;
    private String note;
    private int ammount;

    private String post_key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();

        String uId = mUser.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uId);

        recyclerView = myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        expenseTotalSum = myview.findViewById(R.id.expense_txt_result);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalvalue = 0;

                for (DataSnapshot mysnapshot : snapshot.getChildren()) {


                    Data data = mysnapshot.getValue(Data.class);

                    totalvalue += data.getAmount();

                    String stTotalvalue = String.valueOf(totalvalue);

                    expenseTotalSum.setText(stTotalvalue);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fetchExpenseData();


        return myview;
    }

    private void fetchExpenseData() {

        Query query = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(mAuth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query, new SnapshotParser<Data>() {
                    @NonNull
                    @Override
                    public Data parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Data(Integer.parseInt(snapshot.child("amount").getValue().toString()),
                                snapshot.child("type").getValue().toString(),
                                snapshot.child("note").getValue().toString(),
                                snapshot.child("id").getValue().toString(),
                                snapshot.child("date").getValue().toString());
                    }
                })
                .build();

        adapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setTxtDate(model.getDate());
                holder.setTxtType(model.getType());
                holder.setTxtDesc(model.getNote());
                holder.setTxtAmmount(model.getAmount());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();
                        type = model.getType();
                        note = model.getNote();
                        ammount = model.getAmount();


                        updateDataItem();
                    }
                });


            }


            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.expense_recycler_data, parent, false);
                    return new ExpenseViewHolder(view);
                }
            }


        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_expanse);
            mDate.setText(date);
        }

        private void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_expanse);
            mType.setText(type);
        }

        private void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.type_txt_expanse);
            mNote.setText(note);
        }

        private void setAmount(int ammount) {
            TextView mAmmount = mView.findViewById(R.id.ammount_txt_expense);
            String strammount = String.valueOf(ammount);

            mAmmount.setText(strammount);
        }


    }

    private void updateDataItem() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);

        myDialog.setView(myview);

        edtAmmount = myview.findViewById(R.id.ammount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        //Set Data to edit text

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());


        btnDelete = myview.findViewById(R.id.btnDelete);
        btnUpdate = myview.findViewById(R.id.btnUpdate);

        final AlertDialog dialog = myDialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String stammount = String.valueOf(ammount);

                stammount= edtAmmount.getText().toString().trim();

                int intamount= Integer.parseInt(stammount);

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(intamount, type, note,post_key,mDate);

                mExpenseDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpenseDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();

    }


}