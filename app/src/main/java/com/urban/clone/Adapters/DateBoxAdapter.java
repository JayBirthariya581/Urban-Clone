package com.urban.clone.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.urban.clone.Helpers.CustomProgressDialog;
import com.urban.clone.R;
import com.urban.clone.databinding.CardDateBoxBinding;
import com.urban.clone.model.DateBoxModel;
import com.urban.clone.model.TimeBoxModel;

import java.util.ArrayList;

public class DateBoxAdapter extends RecyclerView.Adapter<DateBoxAdapter.MyViewHolder> {
    ArrayList<DateBoxModel> mlist;
    Context context;
    int selectedPosition = -1;
    TimeBoxAdapter timeBoxAdapter;

    CustomProgressDialog dialog;
    int mechanicCount, hourOfDay, dateOfDay;

    public DateBoxAdapter(ArrayList<DateBoxModel> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public CustomProgressDialog getDialog() {
        return dialog;
    }

    public void setDialog(CustomProgressDialog dialog) {
        this.dialog = dialog;
    }

    public int getDateOfDay() {
        return dateOfDay;
    }

    public void setDateOfDay(int dateOfDay) {
        this.dateOfDay = dateOfDay;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMechanicCount() {
        return mechanicCount;
    }

    public void setMechanicCount(int mechanicCount) {
        this.mechanicCount = mechanicCount;
    }

    public TimeBoxAdapter getTimeBoxAdapter() {
        return timeBoxAdapter;
    }

    public void setTimeBoxAdapter(TimeBoxAdapter timeBoxAdapter) {
        this.timeBoxAdapter = timeBoxAdapter;
    }


    public ArrayList<DateBoxModel> getMlist() {
        return mlist;
    }

    public void setMlist(ArrayList<DateBoxModel> mlist) {
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_date_box, parent, false);

        return new DateBoxAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DateBoxModel m = mlist.get(position);
        holder.binding.date.setText(m.getDate());
        holder.binding.day.setText(m.getDay());


        if (selectedPosition == position) {
            holder.itemView.setSelected(true);
            holder.binding.date.setTextColor(Color.parseColor("#4a60d0"));
            holder.binding.day.setTextColor(Color.parseColor("#4a60d0"));
        } else {
            holder.itemView.setSelected(false);
            holder.binding.date.setTextColor(Color.parseColor("#000000"));
            holder.binding.day.setTextColor(Color.parseColor("#7c7c7c"));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                selectedPosition = position;
                notifyDataSetChanged();

                timeBoxAdapter.getMlist().clear();
                timeBoxAdapter.setSelectedPosition(-1);

                DatabaseReference slots = FirebaseDatabase.getInstance().getReference("AppManager").child("SlotManager")
                        .child("Slots").child(m.getDate());

                slots.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot date) {
                        int j;
                        String z;
                        if (date.exists()) {//if selected date exists in database

                            if (String.valueOf(dateOfDay).equals(m.getDate())) { //if its current day
                                for (int t = 9; t < 20; t += 3) {

                                    if (t > 12) {
                                        j = t - 12;
                                        z = " pm";

                                    } else if (t == 12) {
                                        j = t;
                                        z = " pm";
                                    } else {
                                        j = t;
                                        z = " am";
                                    }
                                    if (hourOfDay <= (t - 2)) {


                                        if (date.child(String.valueOf(j)).exists()) {//if this(j) time slot exists for that day

                                            if (date.child(String.valueOf(j)).getChildrenCount() < mechanicCount) {//if a mechanic is available

                                                timeBoxAdapter.getMlist().add(new TimeBoxModel(String.valueOf(j) + z, "Available"));


                                            } else {
                                                timeBoxAdapter.getMlist().add(new TimeBoxModel(String.valueOf(j) + z, "NotAvailable"));
                                            }

                                        } else {//if time slot doesn't exist in database
                                            timeBoxAdapter.getMlist().add(new TimeBoxModel(String.valueOf(j) + z, "Available"));
                                        }


                                    } else {
                                        timeBoxAdapter.getMlist().add(new TimeBoxModel(String.valueOf(j) + z, "NotAvailable"));
                                    }

                                }
                            } else {//if its not current day
                                for (int t = 9; t < 16; t += 3) {

                                    if (t > 12) {
                                        j = t - 12;
                                        z = " pm";

                                    } else if (t == 12) {
                                        j = t;
                                        z = " pm";
                                    } else {
                                        j = t;
                                        z = " am";
                                    }


                                    if (date.child(String.valueOf(j)).exists()) {//if this(j) time slot exists for that day

                                        if (date.child(String.valueOf(j)).getChildrenCount() < mechanicCount) {//if a mechanic is available

                                            timeBoxAdapter.getMlist().add(new TimeBoxModel(String.valueOf(j) + z, "Available"));


                                        } else {
                                            timeBoxAdapter.getMlist().add(new TimeBoxModel(String.valueOf(j) + z, "NotAvailable"));
                                        }

                                    } else {//if time slot doesn't exist in database
                                        timeBoxAdapter.getMlist().add(new TimeBoxModel(String.valueOf(j) + z, "Available"));
                                    }
                                }

                            }
                        } else {//when selected date is not in database

                            if (String.valueOf(dateOfDay).equals(m.getDate())) {//if its current day
                                for (int t = 9; t < 20; t += 3) {

                                        if (t > 12) {
                                            j = t - 12;
                                            z = " pm";

                                        } else if (t == 12) {
                                            j = t;
                                            z = " pm";
                                        } else {
                                            j = t;
                                            z = " am";
                                        }
                                    if (hourOfDay <= (t - 2)) {//show slots for two hours later from now
                                        timeBoxAdapter.getMlist().add(new TimeBoxModel(String.valueOf(j) + z, "Available"));
                                    }else{
                                        timeBoxAdapter.getMlist().add(new TimeBoxModel(String.valueOf(j) + z, "NotAvailable"));
                                    }
                                }

                            } else {//when not current day
                                for (int t = 9; t < 16; t += 3) {
                                    if (t > 12) {
                                        j = t - 12;
                                        z = " pm";

                                    } else if (t == 12) {
                                        j = t;
                                        z = " pm";
                                    } else {
                                        j = t;
                                        z = " am";
                                    }

                                    timeBoxAdapter.getMlist().add(new TimeBoxModel(String.valueOf(j) + z, "Available"));

                                }


                            }


                        }

                        timeBoxAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CardDateBoxBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = CardDateBoxBinding.bind(itemView);

        }
    }
}
