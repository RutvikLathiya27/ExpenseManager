package com.example.expensemanager.Fragments;

import android.app.AlertDialog;
import android.opengl.ETC1;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.ExpenseViewHolder;
import com.example.expensemanager.Model.Data;
import com.example.expensemanager.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class DashBoardFragment extends Fragment {

    //Floating button

    private FloatingActionButton fab_main_btn, fab_income_btn, fab_expense_btn;

    //flating Button text

    private TextView fab_income_text, fab_expense_text;

    //boolean

    private boolean isopen = false;

    //animation class object

    private Animation FadOpen, FadClose;

    //Firebase...

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Dashboard income And Expense

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //Recycler View

    private RecyclerView mRecyclerIncome, mRecyclerExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        //connect btn and text

        fab_main_btn = myview.findViewById(R.id.fab_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expanse_fy_btn);

        fab_income_text = myview.findViewById(R.id.income_ft_text);
        fab_expense_text = myview.findViewById(R.id.expense_ft_text);

        //Animation

        FadOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        //Recycler

        mRecyclerIncome = myview.findViewById(R.id.recycler_income);
        mRecyclerExpense = myview.findViewById(R.id.recycler_expense);


        //connect total income and expense

        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);


        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if (isopen) {
                    fab_income_btn.startAnimation(FadClose);
                    fab_expense_btn.startAnimation(FadClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_text.startAnimation(FadClose);
                    fab_expense_text.startAnimation(FadClose);
                    fab_income_text.setClickable(false);
                    fab_expense_text.setClickable(false);

                    isopen = false;
                } else {
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_text.startAnimation(FadOpen);
                    fab_expense_text.startAnimation(FadOpen);
                    fab_income_text.setClickable(true);
                    fab_expense_text.setClickable(true);
                    isopen = true;
                }


            }
        });

        //calculate total income..

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum = 0;

                for (DataSnapshot mysnap : snapshot.getChildren()) {

                    Data data = mysnap.getValue(Data.class);

                    totalsum += data.getAmount();

                    String stResult = String.valueOf(totalsum);

                    totalIncomeResult.setText(stResult);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //calculate total expense

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum = 0;

                for (DataSnapshot mysnap : snapshot.getChildren()) {

                    Data data = mysnap.getValue(Data.class);

                    totalsum += data.getAmount();

                    String stTotalSum = String.valueOf(totalsum);

                    totalExpenseResult.setText(stTotalSum);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        fetchIncomeData();
        fetchExpenseData();

        return myview;
    }



    //Floating button Animation

    private void ftAnimation() {

        if (isopen) {
            fab_income_btn.startAnimation(FadClose);
            fab_expense_btn.startAnimation(FadClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_text.startAnimation(FadClose);
            fab_expense_text.startAnimation(FadClose);
            fab_income_text.setClickable(false);
            fab_expense_text.setClickable(false);

            isopen = false;
        } else {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_text.startAnimation(FadOpen);
            fab_expense_text.startAnimation(FadOpen);
            fab_income_text.setClickable(true);
            fab_expense_text.setClickable(true);
            isopen = true;
        }

    }

    private void addData() {

        //Fab Button Income

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                incomeDataInsert();

            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expenseDataInsert();

            }
        });

    }

    public void incomeDataInsert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myView);

        AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText edtAmmount = myView.findViewById(R.id.ammount_edt);
        EditText edtType = myView.findViewById(R.id.type_edt);
        EditText edtNote = myView.findViewById(R.id.note_edt);

        Button btnSave = myView.findViewById(R.id.btnSave);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = edtType.getText().toString().trim();
                String ammount = edtAmmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    edtType.setError("Required Field..");
                    return;
                }
                if (TextUtils.isEmpty(ammount)) {
                    edtAmmount.setError("Required Field..");
                    return;
                }

                int ourammountint = Integer.parseInt(ammount);

                if (TextUtils.isEmpty(note)) {
                    edtNote.setError("Required Field..");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());


                Data data = new Data(ourammountint, type, note, id, mDate);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data ADDED", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    public void expenseDataInsert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText amount = myview.findViewById(R.id.ammount_edt);
        EditText type = myview.findViewById(R.id.type_edt);
        EditText note = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmAmmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote = note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmmount)) {
                    amount.setError("Required Field..");
                    return;
                }

                int inamount = Integer.parseInt(tmAmmount);

                if (TextUtils.isEmpty(tmType)) {
                    type.setError("Required Field..");
                    return;
                }
                if (TextUtils.isEmpty(tmNote)) {
                    note.setError("Required Field..");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(inamount, tmType, tmNote, id, mDate);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data ADDED", Toast.LENGTH_SHORT).show();


                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void fetchIncomeData() {

        Query query = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(mAuth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query, new SnapshotParser<Data>() {
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

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                (options) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {

                holder.setIncomeType(model.getType());
                holder.setIncomeAmmount(String.valueOf(model.getAmount()));
                holder.setIncomeDate(model.getDate());

            }

            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false);

                return new IncomeViewHolder(view);
            }


        };

        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

    }

    private void fetchExpenseData() {

        Query query = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(mAuth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query, new SnapshotParser<Data>() {
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

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>
                (options) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {

                holder.setExpenseType(model.getType());
                holder.setExpenseAmmount(String.valueOf(model.getAmount()));
                holder.setExpenseDate(model.getDate());

            }

            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false);

                return new ExpenseViewHolder(view);
            }


        };

        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();


    }
    

    //For Income Data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {

        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);

            mIncomeView = itemView;
        }

        public void setIncomeType(String type) {
            TextView mType = mIncomeView.findViewById(R.id.type_Income_ds);
            mType.setText(type);
        }

        public void setIncomeAmmount(String ammount) {
            TextView mAmmount = mIncomeView.findViewById(R.id.ammount_Income_ds);

            String strAmmount = String.valueOf(ammount);
            mAmmount.setText(strAmmount);
        }

        public void setIncomeDate(String date) {
            TextView mDate = mIncomeView.findViewById(R.id.date_Income_ds);
            mDate.setText(date);
        }

    }

    //For Expense Data

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView=itemView;

        }

        public void setExpenseType(String type) {
            TextView mType = mExpenseView.findViewById(R.id.type_Expense_ds);
            mType.setText(type);
        }

        public void setExpenseAmmount(String ammount) {
            TextView mAmmount = mExpenseView.findViewById(R.id.ammount_Expense_ds);

            String strAmmount = String.valueOf(ammount);
            mAmmount.setText(strAmmount);
        }

        public void setExpenseDate(String date) {
            TextView mDate = mExpenseView.findViewById(R.id.date_Expense_ds);
            mDate.setText(date);
        }

    }


}