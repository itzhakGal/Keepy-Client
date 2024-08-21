package com.example.keepy.app.activity.kindergartenScreens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.keepy.R;

public class DetailsFragment extends Fragment {

    private String currentUserPhoneNumber;
    private String kindergartenName;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String currentUserPhoneNumber, String kindergartenName) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("currentUserPhoneNumber", currentUserPhoneNumber);
        args.putString("kindergartenName", kindergartenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserPhoneNumber = getArguments().getString("currentUserPhoneNumber");
            kindergartenName = getArguments().getString("kindergartenName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        // Set up title and icons
        TextView title = view.findViewById(R.id.tvTitleDetails);
        title.setText("Keeping an Eye Out");

        // You can dynamically set images or texts for the icons if needed
        ImageView iconKids = view.findViewById(R.id.iconKidsDetails);
        ImageView iconParents = view.findViewById(R.id.iconParentsDetails);
        ImageView iconKindergarten = view.findViewById(R.id.iconKindergartenDetails);
        TextView tvKindergartenName = view.findViewById(R.id.tvKindergartenNameValue);
        TextView tvKindergartenersPhone = view.findViewById(R.id.tvKindergartenPhoneValue);

        // Set up the kindergarten name and phone number
        tvKindergartenName.setText(kindergartenName);
        tvKindergartenersPhone.setText("0000000");

        // Set up the table
        TableLayout tableLayout = view.findViewById(R.id.tableLayoutDetails);

        // Add more rows dynamically if needed
        // This example adds a row dynamically (you can remove this part if not needed)
        TableRow newRow = new TableRow(getContext());
        TextView nameTextView = new TextView(getContext());
        nameTextView.setText("Yoni");

        TextView addressTextView = new TextView(getContext());
        addressTextView.setText("Tel Aviv");

        TextView parentTextView = new TextView(getContext());
        parentTextView.setText("Sarah");

        newRow.addView(nameTextView);
        newRow.addView(addressTextView);
        newRow.addView(parentTextView);

        tableLayout.addView(newRow);

        return view;
    }
}
