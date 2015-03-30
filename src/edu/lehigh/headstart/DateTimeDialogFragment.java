/*==============================================================================
 Copyright (c) 2013-2014 Li Tian, Lehigh University
 All Rights Reserved.
 ==============================================================================*/

package edu.lehigh.headstart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class DateTimeDialogFragment extends DialogFragment
						implements OnDateChangedListener, OnTimeChangedListener {
	
    // Define constants for date-time picker.
    public final static int DATE_PICKER = 1;
    public final static int TIME_PICKER = 2;
    public final static int DATE_TIME_PICKER = 3;

    // DatePicker reference
    private DatePicker datePicker;

    // TimePicker reference
    private TimePicker timePicker;

    // Calendar reference
    private Calendar mCalendar;

    // Define root view
    private TextView v_root;

    // Define Dialog type
    private int mDialogType;

    // Define Dialog view
    private View mView;
    
    // RadioGroup
    private int[] radioGroups={
    		R.id.my_parenting_skills_are_effective, R.id.meet_my_childs_needs, R.id.soothe_my_child, R.id.avoid_using_physical_punishment,
    		R.id.understand_my_childs_needs, R.id.my_child_may_not_always_do_as_I_say, R.id.spend_special_time_with_my_child,
    		R.id.my_childs_behavior_has_meaning,R.id.learn_by_exploring_the_environment,
    		R.id.We_have_family_rules_and_daily_routines_that_we_follow,
    		R.id.I_create_activities_at_home_to_support_my_childs_learning,R.id.developing_a_positive_relationship,
    		R.id.I_have_family_members_and_or_friends,
    		R.id.My_child_spends_time_playing_with_other_children,R.id.I_have_a_positive_relationship_with_my_neighbors,
    		R.id.I_like_exploring_my_community_with_my_family,
    		R.id.I_participate_in_community_groups_such_as_church,R.id.I_utilize_other_agency_supports,
    		R.id.I_have_someone_to_care_for_my_child_when_needed,
    		R.id.I_feel_my_children_access_to_quality,R.id.I_feel_safe_in_my_home_and_community,
    		R.id.I_am_satisfied_with_my_current_living_situation,
    		R.id.My_home_is_safe_and_in_good_physical_condition,R.id.meet_basic_needs_such_as_food_and_housing,
    		R.id.I_pay_my_bills_regularly_and_on_time,R.id.I_am_satisfied_with_my_current_employment_status,R.id.My_family_health_needs_are_met,
    		R.id.I_know_how_to_plan_nutritious_family_meals,R.id.I_feel_safe_in_my_closest_relationships,
    		R.id.cope_with_the_daily_frustrations_of_life,
    		R.id.advocate_for_what_is_best_for_my_child_and_family,R.id.participate_in_Parent_Teacher_Conferences,
    		R.id.a_leader_in_a_community_group,
    		R.id.comfortable_talking_in_a_large_group,R.id.research_information_to_support_my_familys_needs,
    		R.id.I_have_a_voice_and_can_change_my_community,
    		R.id.opportunities_to_better_myself_and_my_family,R.id.crayons_pencils_and_paper_available,R.id.access_to_at_least_10_picture_books,
    		R.id.read_a_picture_book_with_my_child,R.id.have_detailed_and_informative_conversations,R.id.sees_me_reading_books,
    		R.id.a_good_reader_and_feel_comfortable,R.id.my_child_will_work_to_his_or_her_potential
    };

    // Constructor start
    public DateTimeDialogFragment(View v, int dialogType) {
        v_root = (TextView)v;
        mDialogType = dialogType;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        Builder builder = new AlertDialog.Builder(getActivity());

        setCancelable(false);
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.date_time_dialog, null);  

        // Set view and title of dialog
        builder.setView(mView)
        		.setTitle(R.string.date_time_title)
                .setPositiveButton("Set",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User ok the dialog
                            	v_root.setText(getDateTime());
                            	switch(v_root.getId()) {
                            	case R.id.date_of_start_of_year_assessment:
                            		for(int i=0; i<radioGroups.length; i++) {
                                		v_root.getRootView().findViewById(radioGroups[i]).
                                			findViewById(R.id.start_of_year).setVisibility(View.VISIBLE);
                                	}
                            		break;
                            	case R.id.date_of_end_of_year_assessment:
                            		for(int i=0; i<radioGroups.length; i++) {
                                		v_root.getRootView().findViewById(radioGroups[i]).
                                			findViewById(R.id.end_of_year).setVisibility(View.VISIBLE);
                                	}
                            		break;
                            	default:
                            	}
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                DateTimeDialogFragment.this.getDialog().cancel();
                            }
                        }); 

        // Grab a Calendar instance
        mCalendar = Calendar.getInstance();

        // Init date picker
        datePicker = (DatePicker) mView.findViewById(R.id.DatePicker);
        datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);

        // Init time picker
        timePicker = (TimePicker) mView.findViewById(R.id.TimePicker);

        // Set default Calendar and Time Style
        setIs24HourView(false);
        setCalendarViewShown(false);     

        switch (mDialogType) {
        case DATE_PICKER:
            mView.findViewById(R.id.TimePicker_Layout).setVisibility(View.GONE);
            break;
        case TIME_PICKER:
            mView.findViewById(R.id.DatePicker_layout).setVisibility(View.GONE);
            break;
        default:
        }
        
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        timePicker.setOnTimeChangedListener(this);
    }

    // Convenience wrapper for internal Calendar instance
    public int get(final int field) {
        return mCalendar.get(field);
    }

    // Convenience wrapper for internal Calendar instance
    public long getDateTimeMillis() {
        return mCalendar.getTimeInMillis();
    }
    
 // Convenience wrapper for internal Calendar instance
    public void getCurrentTime() {
        mCalendar.clear();
    }

    // Convenience wrapper for internal TimePicker instance
    public void setIs24HourView(boolean is24HourView) {
        timePicker.setIs24HourView(is24HourView);
    }

    // Convenience wrapper for internal TimePicker instance
    public boolean is24HourView() {
        return timePicker.is24HourView();
    }

    // Convenience wrapper for internal DatePicker instance
    public void setCalendarViewShown(boolean calendarView) {
        datePicker.setCalendarViewShown(calendarView);
    }

    // Convenience wrapper for internal DatePicker instance
    public boolean CalendarViewShown() {
        return datePicker.getCalendarViewShown();
    }

    // Convenience wrapper for internal DatePicker instance
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        datePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    // Convenience wrapper for internal TimePicker instance
    public void updateTime(int currentHour, int currentMinute) {
        timePicker.setCurrentHour(currentHour);
        timePicker.setCurrentMinute(currentMinute);
    }

    public String getDateTime() {
    	SimpleDateFormat sdf;
    	switch (mDialogType) {
        case DATE_PICKER:
        	sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            break;
        case TIME_PICKER:
        	sdf = new SimpleDateFormat("KK:mm a", Locale.US);
            break;
        default:
        	sdf = new SimpleDateFormat("MM/dd/yy KK:mm a", Locale.US);
        }
        
        return sdf.format(mCalendar.getTime());
    }

    // Called every time the user changes DatePicker values
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // Update the internal Calendar instance
        mCalendar.set(year, monthOfYear, dayOfMonth, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
    }

    // Called every time the user changes TimePicker values
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        // Update the internal Calendar instance
        mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
    }
}