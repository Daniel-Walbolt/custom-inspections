package daniel.walbolt.custominspections.Adapters.ScheduleRecycler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import daniel.walbolt.custominspections.Activities.InspectionActivity;
import daniel.walbolt.custominspections.Adapters.ItemTouchHelperAdapter;
import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ConfirmAlert;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.ClientDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
import daniel.walbolt.custominspections.Libraries.FirebaseBusiness;
import daniel.walbolt.custominspections.R;

public class InspectionScheduleRecyclerAdapter extends RecyclerView.Adapter<InspectionScheduleRecyclerAdapter.ScheduleHolder> implements ItemTouchHelperAdapter
{

    private ItemTouchHelper mTouchHelper;

    ArrayList<Schedule> mSchedules;
    Activity mActivity;

    private boolean isPastInspections;

    public InspectionScheduleRecyclerAdapter(ArrayList<Schedule> schedules, Activity activity, final RecyclerView thisRecyclerView, final TextView emptyView, boolean isPastInspections)
    {
        mSchedules = schedules;
        mActivity = activity;
        this.isPastInspections = isPastInspections;

        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if(getItemCount() == 0)
            {

                emptyView.setVisibility(View.VISIBLE);
                thisRecyclerView.setVisibility(View.GONE);

            }
            else
            {

                emptyView.setVisibility(View.GONE);
                thisRecyclerView.setVisibility(View.VISIBLE);

            }

        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if(getItemCount() == 0)
            {

                emptyView.setVisibility(View.VISIBLE);
                thisRecyclerView.setVisibility(View.GONE);

            }
            else
            {

                emptyView.setVisibility(View.GONE);
                thisRecyclerView.setVisibility(View.VISIBLE);

            }

        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if(getItemCount() == 0)
            {

                emptyView.setVisibility(View.VISIBLE);
                thisRecyclerView.setVisibility(View.GONE);

            }
            else
            {

                emptyView.setVisibility(View.GONE);
                thisRecyclerView.setVisibility(View.VISIBLE);

            }
        }
        });
    }

    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View v;

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inspection_schedule_recycler, parent, false);

        return new ScheduleHolder(v, isPastInspections);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleHolder holder, int position)
    {

        Schedule schedule = mSchedules.get(position);
        holder.month.setText(schedule.date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        String day = String.valueOf(schedule.date.get(Calendar.DAY_OF_MONTH));
        if (day.startsWith("1") && day.length() == 2) // If the date is 10-19, add "th"
            day += "th";
        else if(day.endsWith("3")) // If the date ends with a 3 and is not 13, add "rd"
            day+= "rd";
        else if(day.endsWith("2")) // If the date ends with a 2 and is not 12, add "nd"
            day+= "nd";
        else if(day.endsWith("1")) // If the date ends with a 1 and is not 11, add "st"
            day+= "st";
        else
            day+="th"; // If the date is not 13, 12, 11 and does not end with 3, 2, or 1. Add "th"

        holder.day.setText(day);
        holder.year.setText(String.valueOf(schedule.date.get(Calendar.YEAR)));
        holder.address.setText(schedule.address);

        holder.client.setText(schedule.client_first + " " + schedule.client_last);

        int dayHour = schedule.date.get(Calendar.HOUR_OF_DAY) > 12 ? schedule.date.get(Calendar.HOUR_OF_DAY) - 12 : schedule.date.get(Calendar.HOUR_OF_DAY);
        String timeFormat = schedule.date.get(Calendar.HOUR_OF_DAY) < 12 || schedule.date.get(Calendar.HOUR_OF_DAY) == 24 ?  " AM" : " PM";
        String dayMinute = schedule.date.get(Calendar.MINUTE) < 10 ? "0" + schedule.date.get(Calendar.MINUTE) : String.valueOf(schedule.date.get(Calendar.MINUTE));
        String startTime = dayHour + ":" + dayMinute + timeFormat;

        holder.start.setText(startTime);

        if(!isPastInspections)
            if(!schedule.isSigned)
            {

                holder.signed.setText("Unsigned");
                holder.signed.setTextColor(Color.RED);

            }
            else
            {

                holder.signed.setText("Signed");
                holder.signed.setTextColor(ContextCompat.getColor(mActivity, R.color.system_complete));

            }


    }

    @Override
    public int getItemCount() {
        return mSchedules.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemSwiped(int position)
    {

        //Show a confirmation dialog to the user when the schedule is swiped.
        new ConfirmAlert(mActivity, "Are you sure you want to delete this schedule?", (v) -> {
            new FirebaseBusiness().removeSchedule(mSchedules.get(position), mActivity);
            mSchedules.remove(position);
            notifyItemRemoved(position);
        }, (v) -> {
            notifyItemChanged(position);
        });

    }

    public void setTouchHelper(ItemTouchHelper helper)
    {

        this.mTouchHelper = helper;

    }

    class ScheduleHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener
    {

        boolean activated = false;
        boolean openCloseDisabled = false;

        TextView month;
        TextView day;
        TextView year;
        TextView address;
        TextView start;
        TextView openClose;
        TextView client;
        TextView info;
        Button begin;
        TextView signed;

        LinearLayout content;

        GestureDetector mGestureDetector;

        public ScheduleHolder(View itemView, boolean isPastInspection)
        {

            super(itemView);
            month = itemView.findViewById(R.id.recycler_schedule_month);
            day = itemView.findViewById(R.id.recycler_schedule_day);
            year = itemView.findViewById(R.id.recycler_schedule_year);
            address = itemView.findViewById(R.id.recycler_schedule_address);
            start = itemView.findViewById(R.id.recycler_schedule_start);

            openClose = itemView.findViewById(R.id.recycler_schedule_open_close);
            openClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    if(!openCloseDisabled) // The button is disabled when an animation is playing, as to avoid visual problems
                    {

                        Animation animation = getAnimation();

                        if(!activated)
                            content.setVisibility(View.VISIBLE);

                        content.startAnimation(animation); // Start the Open/Close animation

                        activated = !activated;

                        if(activated) // Set the icon to show if the content is display or not
                            openClose.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
                        else
                            openClose.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);



                    }

                }
            });
            client = itemView.findViewById(R.id.recycler_schedule_client);
            info = itemView.findViewById(R.id.recycler_schedule_info);
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ClientDialog(mActivity, mSchedules.get(getAdapterPosition()));
                }
            });
            begin = itemView.findViewById(R.id.recycler_schedule_begin); // Get the button that starts an inspection from a schedule.
            begin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inspection = new Intent(mActivity, InspectionActivity.class);
                    inspection.putExtra("MySchedule", mSchedules.get(getAdapterPosition()));
                    mActivity.startActivity(inspection);
                }
            });

            content = itemView.findViewById(R.id.recycler_schedule_content);
            content.setVisibility(View.GONE);

            if(!isPastInspection)
                signed = itemView.findViewById(R.id.recycler_schedule_signed);

            mGestureDetector = new GestureDetector(itemView.getContext(), this);
            itemView.setOnTouchListener(this);

        }

        //Method to get the correct animation based off whether or not the content is visible
        private Animation getAnimation()
        {

            Animation animation;

            final boolean isSlideDown = !activated; // Temporary variable to store the instantaneous value of "activated"

            //Check if the content is visible
            if(content.getVisibility() == View.VISIBLE) //Default value is false, the content is not visible
                animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.slide_up);
            else
                animation =  AnimationUtils.loadAnimation(itemView.getContext(), R.anim.slide_down);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if(isSlideDown)
                        content.setVisibility(View.VISIBLE); // Set the content visible when the animation is SLIDE_DOWN
                    openCloseDisabled = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(!isSlideDown)
                        content.setVisibility(View.GONE); // Hide the content after the animation when the animation is SLIDE_UP
                    openCloseDisabled = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            return animation;

        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            mTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {

            mGestureDetector.onTouchEvent(event);
            return true;

        }
    }

}
