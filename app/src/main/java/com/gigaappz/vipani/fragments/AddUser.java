package com.gigaappz.vipani.fragments;

import android.content.Context;
import android.net.Uri;
import android.opengl.ETC1;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gigaappz.vipani.R;
import com.gigaappz.vipani.interfaces.NewUserDataAdded;
import com.gigaappz.vipani.models.UserModel;

import static com.gigaappz.vipani.fragments.UsersTab.userModels;


public class AddUser extends Fragment implements View.OnClickListener {

    public AddUser() {
        // Required empty public constructor
    }

    private Context context;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
    }

    private EditText firstValue;
    private EditText secondValue;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView   = inflater.inflate(R.layout.add_user, container, false);
       /* firstValue = rootView.findViewById(R.id.first_et);
        secondValue= rootView.findViewById(R.id.second_et);
        Button addButton    = rootView.findViewById(R.id.add_button);
        Button cancelButton = rootView.findViewById(R.id.cancel_button);

        addButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);*/
        return rootView;
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.add_button:
                UserModel model = new UserModel();
                model.setUserName(firstValue.getText().toString());
                model.setUserMobile(secondValue.getText().toString());
                userModels.add(model);

                dataAdded.onNewUserDataAdded();
                break;
            case R.id.cancel_button:
                dataAdded.onNewUserDataAdded();
                break;
        }*/
    }

    private static NewUserDataAdded dataAdded;
    public void onNewUserDataAdded(NewUserDataAdded dataAdded){
        AddUser.dataAdded   = dataAdded;
    }
}
