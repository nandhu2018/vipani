package com.gigaappz.vipani.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gigaappz.vipani.R;
import com.gigaappz.vipani.interfaces.NewDomesticDataSaved;
import com.gigaappz.vipani.models.DomesticValueModel;

import static com.gigaappz.vipani.fragments.DomesticTab.domesticValueModels;


public class AddDomesticValues extends Fragment {

    public AddDomesticValues(){
//        Empty Constructor
    }

    private Context context;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    private EditText firstForm;
    private EditText secondForm;
    private EditText thirdForm;
    private EditText fourthForm;
    private EditText fifthForm;
    private EditText sixthForm;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.add_domestic_values, container, false);
       /* firstForm      = view.findViewById(R.id.first_value);
        secondForm     = view.findViewById(R.id.second_value);
        thirdForm      = view.findViewById(R.id.third_value);
        fourthForm     = view.findViewById(R.id.fourth_value);
        fifthForm      = view.findViewById(R.id.fifth_value);
        sixthForm      = view.findViewById(R.id.sixth_value);
        Button okButton         = view.findViewById(R.id.save_button);
        Button cancelButton     = view.findViewById(R.id.dialog_cancel_button);

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);*/
        return view;
    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_button:
                DomesticValueModel model    = new DomesticValueModel();
                model.setHeadText(firstForm.getText().toString());
                model.setSubHeadText(secondForm.getText().toString());
                model.setValueText(thirdForm.getText().toString());
                model.setValueSubText(fourthForm.getText().toString());
                model.setValueRateText(fifthForm.getText().toString());
                model.setValueDiffText(sixthForm.getText().toString());
                model.setTime("time:");
                model.setProfit(true);
                domesticValueModels.add(model);

                dataSaved.onNewDomesticDataSave();
                break;
            case R.id.dialog_cancel_button:
                dataSaved.onNewDomesticDataSave();
                break;
        }
    }

    private static NewDomesticDataSaved dataSaved;
    public void setOnNewDomesticDataSaved(NewDomesticDataSaved dataSaved){
        AddDomesticValues.dataSaved = dataSaved;
    }*/
}
