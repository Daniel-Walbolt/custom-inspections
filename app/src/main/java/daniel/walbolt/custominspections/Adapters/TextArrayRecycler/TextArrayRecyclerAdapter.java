package daniel.walbolt.custominspections.Adapters.TextArrayRecycler;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.TextInputDialog;
import daniel.walbolt.custominspections.R;

public class TextArrayRecyclerAdapter extends RecyclerView.Adapter<TextArrayRecyclerAdapter.ViewHolder>
{

    private ArrayList<String> list;
    private TextView listSize; // This view displays the total number of items in the list

    public TextArrayRecyclerAdapter(ArrayList<String> list, TextView listSize)
    {

        this.list = list; // Store the reference to the list that is beign edited and displayed
        this.listSize = listSize;

        listSize.setText("Count: " + list.size());

        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged()
            {

                listSize.setText("Count: " + list.size());

            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {



            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount)
            {


            }
        });


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create the controller for the view
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.text_array_item_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {

        String text = "Add Text";

        //Display the content of the list when the content exists
        if(position < list.size())
            text = list.get(position);

        holder.title.setText(text);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Position : " + position);
            }
        });

        if(position == list.size()) // If the item being displayed is meant to be the "Add Item" button
        {
            holder.delete.setVisibility(View.GONE);
            //Get text input from the user using custom dialog and nested listeners
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextInputDialog textInput = new TextInputDialog(holder.itemView.getContext(), "Input Text", "", "");
                    textInput.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            list.add(textInput.getText());
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyItemRemoved(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView title;
        ImageButton delete;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            title = itemView.findViewById(R.id.text_array_item_title);
            delete = itemView.findViewById(R.id.text_array_item_delete);

        }
    }

}
