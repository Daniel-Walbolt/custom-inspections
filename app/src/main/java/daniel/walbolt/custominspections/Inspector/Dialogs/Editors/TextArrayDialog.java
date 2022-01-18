package daniel.walbolt.custominspections.Inspector.Dialogs.Editors;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Adapters.TextArrayRecycler.TextArrayRecyclerAdapter;
import daniel.walbolt.custominspections.R;

public class TextArrayDialog extends Dialog
{

    /*

    This dialog shows the contents of a list using a recycler view.
    The user can edit the content in a user friendly fashion, and total items is automatically calculated.

     */

    private TextView listSize;
    private RecyclerView list;
    private ArrayList<String> strings;

    public TextArrayDialog(@NonNull Context context, ArrayList<String> list)
    {

        super(context);

        this.strings = list;

        setContentView(R.layout.text_array_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);

        listSize = findViewById(R.id.text_array_dialog_count);
        initRecycler();


        this.strings = list; // Store the reference to the list being edited

        show();

    }

    private void initRecycler()
    {

        list = findViewById(R.id.text_array_dialog_list);

        TextArrayRecyclerAdapter adapter = new TextArrayRecyclerAdapter(strings, listSize);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        list.setAdapter(adapter);
        list.setLayoutManager(manager);
        list.setNestedScrollingEnabled(false);


    }

}
