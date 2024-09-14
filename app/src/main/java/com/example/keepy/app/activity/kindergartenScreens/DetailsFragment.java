package com.example.keepy.app.activity.kindergartenScreens;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.keepy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsFragment extends Fragment {

    private String currentUserPhoneNumber;
    private String kindergartenName;
    private TableLayout tableLayout;
    private RatingBar ratingBar;
    private TextView ratingDescription;
    private View cardTable;
    private View cardRating;

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
        tableLayout = view.findViewById(R.id.tableLayoutDetails);
        cardTable = view.findViewById(R.id.cardTable);
        cardRating = view.findViewById(R.id.cardRating);
        ratingBar = view.findViewById(R.id.ratingBar);
        ratingDescription = view.findViewById(R.id.ratingDescription);

        // Set up ImageViews
        ImageView iconKids = view.findViewById(R.id.iconKidsDetails);
        ImageView iconParents = view.findViewById(R.id.iconParentsDetails);
        ImageView iconKindergarten = view.findViewById(R.id.iconKindergartenDetails);
        ImageView iconAdditionalDetails = view.findViewById(R.id.iconAdditionalDetails);

        // Set OnClickListener for each ImageView
        iconKids.setOnClickListener(v -> showKidsDetails());
        iconParents.setOnClickListener(v -> showParentsDetails());
        iconKindergarten.setOnClickListener(v -> showStaffDetails());
        iconAdditionalDetails.setOnClickListener(v -> showRatingDetails(4));  // Change the rating value to test different scenarios

        // Default view
        showKidsDetails();

        return view;
    }

    private void showKidsDetails() {
        toggleView(true);
        tableLayout.removeAllViews(); // Clear the table before adding new rows

        // Header Row
        TableRow headerRow = new TableRow(getContext());
        addCellToRow(headerRow, "ChildID", true);
        addCellToRow(headerRow, "First Name", true);
        addCellToRow(headerRow, "Last Name", true);
        addCellToRow(headerRow, "Date Of Birth", true);
        tableLayout.addView(headerRow);

        // Data Rows - Adding 11 children
        addDataRow("1", "Lily", "Williams", "2022-05-14");
        addDataRow("2", "Jack", "Doe", "2023-11-22");
        addDataRow("3", "Emma", "Brown", "2023-01-10");
        addDataRow("4", "Olivia", "Smith", "2022-08-20");
        addDataRow("5", "Noah", "Johnson", "2022-02-14");
        addDataRow("6", "Sophia", "Davis", "2022-03-10");
        addDataRow("7", "Mason", "Miller", "2023-06-01");
        addDataRow("8", "Isabella", "Wilson", "2023-04-18");
        addDataRow("9", "James", "Taylor", "2023-07-05");
        addDataRow("10", "Ava", "Moore", "2023-02-28");
        addDataRow("11", "Logan", "Anderson", "2022-03-15");
    }

    private void showParentsDetails() {
        toggleView(true);
        tableLayout.removeAllViews(); // Clear the table before adding new rows

        // Header Row
        TableRow headerRow = new TableRow(getContext());
        addCellToRow(headerRow, "ParentID", true);
        addCellToRow(headerRow, "Parent Name", true);
        addCellToRow(headerRow, "Phone Number", true);
        addCellToRow(headerRow, "Child Name", true); // New column for Child Name
        tableLayout.addView(headerRow);

        // Data Rows
        addDataRow("1", "John Doe", "050-555-1234", "Lily Doe");
        addDataRow("2", "Jane Smith", "088-555-5678", "Jack Smith");
        addDataRow("3", "Michael Johnson", "077-555-9101", "Emma Johnson");
        addDataRow("4", "Emily Brown", "052-555-1121", "Olivia Brown");
        addDataRow("5", "David Wilson", "050-555-3141", "Noah Wilson");
        addDataRow("6", "Jessica Miller", "050-555-5161", "Sophia Miller");
        addDataRow("7", "Sarah Davis", "077-555-7181", "Mason Davis");
        addDataRow("8", "James Taylor", "052-555-9202", "Isabella Taylor");
        addDataRow("9", "Anna Thompson", "044-555-1222", "Ava Thompson");
        addDataRow("10", "Robert Martinez", "054-555-3242", "Logan Martinez");
        addDataRow("11", "Laura Robinson", "052-555-5262", "Mia Robinson");
        addDataRow("12", "Paul Clark", "077-555-7282", "Lucas Clark");
        addDataRow("13", "Emma Lee", "050-555-9303", "Amelia Lee");
        addDataRow("14", "Andrew Walker", "054-555-1323", "Ethan Walker");
        addDataRow("15", "Olivia Hernandez", "044-555-3343", "Aiden Hernandez");
        addDataRow("16", "Noah Hall", "050-555-5363", "Charlotte Hall");
        addDataRow("17", "Mason Young", "050-555-7383", "Harper Young");
        addDataRow("18", "Sophia Allen", "052-555-9404", "Ella Allen");
        addDataRow("19", "William King", "054-555-1424", "Alexander King");
        addDataRow("20", "Isabella Wright", "054-555-3444", "Abigail Wright");
    }

    private void showStaffDetails() {
        toggleView(true);
        tableLayout.removeAllViews(); // Clear the table before adding new rows

        // Header Row
        TableRow headerRow = new TableRow(getContext());
        addCellToRow(headerRow, "StaffID", true);
        addCellToRow(headerRow, "Staff Name", true);
        addCellToRow(headerRow, "Position", true);
        addCellToRow(headerRow, "Phone Number", true); // New column for Phone Number
        tableLayout.addView(headerRow);

        // Data Rows
        addDataRow("1", "Emily Brown", "Teacher", "050-456-7890");
        addDataRow("2", "John Smith", "Teacher", "052-567-8901");
        addDataRow("3", "Michael Johnson", "Assistant", "052-678-9012");
        addDataRow("4", "Sarah Davis", "Assistant", "052-789-0123");
        addDataRow("5", "David Wilson", "Assistant", "054-890-1234");
        addDataRow("6", "Jessica Miller", "Assistant", "054-901-2345");
        addDataRow("7", "Anna Thompson", "Assistant", "050-012-3456");
    }

/*
    private void showRatingDetails() {
        toggleView(false);

        DatabaseReference reference = FirebaseDatabase.getInstance("https://keepyapp-e4d50-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("kindergartens")
                .child(kindergartenName)
                .child("weekly_positive_feedback")
                .child("star_rating");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    int rating = 2;

                    ratingBar.setRating(rating);

                    String additionalDescription = "";
                    switch (rating) {
                        case 0:
                            additionalDescription = "This week’s analysis shows that the level of encouragement and support was significantly below expectations. We recommend immediate attention to fostering a more positive environment for the children.";
                            break;
                        case 1:
                            additionalDescription = "The data indicates that there was very limited encouragement and support provided to the children this week. There's an opportunity to greatly improve in this area.";
                            break;
                        case 2:
                            additionalDescription = "This week’s feedback suggests that while some encouragement was provided, there is still considerable room for improvement in supporting the children more effectively.";
                            break;
                        case 3:
                            additionalDescription = "The level of encouragement and support provided to the children this week was average. Consistent positive reinforcement would further enhance their experience.";
                            break;
                        case 4:
                            additionalDescription = "This week’s data reflects a good level of encouragement and support for the children. Continuing to build on these strengths could lead to even better outcomes.";
                            break;
                        case 5:
                            additionalDescription = "Excellent! The system shows that the children received a high level of encouragement and support this week. Keep up the great work in fostering a positive environment!";
                            break;
                        default:
                            additionalDescription = "No rating available.";
                            break;
                    }

                    ratingDescription.setText(additionalDescription);
                } else {
                    ratingDescription.setText("No rating available.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ratingDescription.setText("Failed to load rating.");
            }
        });
    }
*/


    private void showRatingDetails(int rating) {
        toggleView(false);

        // Set the rating bar to the provided rating
        ratingBar.setRating(rating);

        // Prepare the appropriate description based on the rating value
        String additionalDescription = "";
        switch (rating) {
            case 0:
                additionalDescription = "This week’s analysis shows that the level of encouragement and support was significantly below expectations. We recommend immediate attention to fostering a more positive environment for the children.";
                break;
            case 1:
                additionalDescription = "The data indicates that there was very limited encouragement and support provided to the children this week. There's an opportunity to greatly improve in this area.";
                break;
            case 2:
                additionalDescription = "This week’s feedback suggests that while some encouragement was provided, there is still considerable room for improvement in supporting the children more effectively.";
                break;
            case 3:
                additionalDescription = "The level of encouragement and support provided to the children this week was average. Consistent positive reinforcement would further enhance their experience.";
                break;
            case 4:
                additionalDescription = "This week’s data reflects a good level of encouragement and support for the children. Continuing to build on these strengths could lead to even better outcomes.";
                break;
            case 5:
                additionalDescription = "Excellent! The system shows that the children received a high level of encouragement and support this week. Keep up the great work in fostering a positive environment!";
                sendPositiveReinforcementNotification();  // Send a notification for rating 5
                break;
            default:
                additionalDescription = "No rating available.";
                break;
        }

        // Set the description text
        ratingDescription.setText(additionalDescription);
    }

    private void sendPositiveReinforcementNotification() {
        String channelId = "positive_reinforcement_channel";
        String channelName = "Positive Reinforcement Notifications";
        String message = "Positive reinforcement benefits everyone. Take a moment to send an encouraging message to the kindergarten team!";

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Prepare the image to be used in the notification
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.stars);  // Replace with your image

        // Build the notification with BigTextStyle to ensure all text is visible
        Notification notification = new NotificationCompat.Builder(getActivity(), channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(largeIcon)
                .setContentTitle("Positive Reinforcement")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))  // Ensure the full message is displayed
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        // Send the notification
        notificationManager.notify(1, notification);
    }




    // Helper method to switch between Table and Rating view
    private void toggleView(boolean showTable) {
        cardTable.setVisibility(showTable ? View.VISIBLE : View.GONE);
        cardRating.setVisibility(showTable ? View.GONE : View.VISIBLE);
    }

    // Helper method to add a row to the table
    private void addDataRow(String... values) {
        TableRow row = new TableRow(getContext());
        for (String value : values) {
            addCellToRow(row, value, false);
        }
        tableLayout.addView(row);
    }

    private void addCellToRow(TableRow row, String value, boolean isHeader) {
        TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setTextColor(isHeader ? getResources().getColor(R.color.myColor) : getResources().getColor(R.color.black));
        textView.setTextSize(isHeader ? 16 : 14);
        textView.setTypeface(null, isHeader ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        textView.setPadding(16, 16, 16, 16);
        row.addView(textView);
    }
}
