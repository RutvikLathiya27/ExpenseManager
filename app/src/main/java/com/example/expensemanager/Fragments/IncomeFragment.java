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

import com.example.expensemanager.Model.Data;
import com.example.expensemanager.R;
import com.example.expensemanager.IncomeViewHolder;
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


public class IncomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    private FirebaseRecyclerAdapter adapter;

    //Recyclerview..
    private RecyclerView recyclerView;

    private TextView incomeTotalSum;

    //Update edit Text

    private EditText edtAmmount, edtType, edtNote;
    private Button btnUpdate, btnDelete;

    //Data item value

    private String type;
    private String note;
    private int amount;

    private String post_key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        recyclerView = myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        incomeTotalSum = myview.findViewById(R.id.income_txt_result);

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                int totalvalue = 0;

                for (DataSnapshot mysnapshot: snapshot.getChildren()){


                    Data data = mysnapshot.getValue(Data.class);

                    totalvalue+=data.getAmount();

                    String stTotalvalue = String.valueOf(totalvalue);

                    incomeTotalSum.setText(stTotalvalue);


                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });


        fetch();


        return myview;
    }

    private void fetch() {

        Query query = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(mAuth.getCurrentUser().getUid());

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

        adapter = new FirebaseRecyclerAdapter< Data, IncomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
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
                        amount = model.getAmount();

                        updateDataItem();
                    }
                });



            }

            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.income_recycler_data, parent, false);
                return new IncomeViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setType(String type) {

            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);

        }

        private void setNote(String note) {

            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setData(String date) {

            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);

        }

        private void setAmmount(int ammount) {

            TextView mAmmount = mView.findViewById(R.id.ammount_txt_income);

            String stammount = String.valueOf(ammount);

            mAmmount.setText(stammount);

        }

    }

    private void updateDataItem(){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.update_data_item, null);

        mydialog.setView(myview);

        edtAmmount = myview.findViewById(R.id.ammount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        //Set Data to edit text

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(amount));
        edtAmmount.setSelection(String.valueOf(amount).length());

        btnDelete = myview.findViewById(R.id.btnDelete);
        btnUpdate = myview.findViewById(R.id.btnUpdate);

        final AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String mdammount = String.valueOf(amount);

                mdammount = edtAmmount.getText().toString().trim();

                int myAmmount = Integer.parseInt(mdammount);

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(myAmmount, type, note, post_key, mDate);

                mIncomeDatabase.child(post_key).setValue(data);

                dialog.dismiss();


            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIncomeDatabase.child(post_key).removeValue();


                dialog.dismiss();

            }
        });

        dialog.show();

    }


}