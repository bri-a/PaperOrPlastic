package edu.pacificu.cs493f15_1.paperorplasticapp.popList;

import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import edu.pacificu.cs493f15_1.paperorplasticapp.R;

/**
 * Created by heyd5159 on 2/6/2016.
 */
public class NewListItemDFragment extends DialogFragment
{
    private Button mbCancel;
    private Button mbOK;
    private EditText mItemNameText;
    private Dialog mDialog;


    public NewListItemDFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.new_item_dialog_fragment, container,
                false);

        // Get field from view
        mItemNameText = (EditText) rootView.findViewById(R.id.item_name_input);

        // Show soft keyboard automatically and request focus to field
        mItemNameText.requestFocus();


        mbCancel = (Button) rootView.findViewById (R.id.cancel_button);
        mbCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mbOK = (Button) rootView.findViewById (R.id.ok_button);
        mbOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoPListActivity activity = (PoPListActivity) getActivity();
                NewItemInfoDialogListener listener = activity.getItemInfoListener();
                listener.onFinishNewItemDialog(mItemNameText.getText().toString());
                mDialog.dismiss();
            }
        });

        mDialog = getDialog();

        mDialog.setTitle("Add Item");

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // Do something else
        return rootView;
    }
}