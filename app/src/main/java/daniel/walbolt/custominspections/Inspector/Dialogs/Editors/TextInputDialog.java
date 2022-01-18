package daniel.walbolt.custominspections.Inspector.Dialogs.Editors;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import daniel.walbolt.custominspections.R;

public class TextInputDialog extends Dialog
{

    private TextView title;
    private EditText text;
    private TextView description; // describes what the text is used for
    private Button confirm;

    public TextInputDialog(@NonNull Context context, String title, String description, String defaultText)
    {

        super(context);

        setContentView(R.layout.text_input_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        this.title = findViewById(R.id.text_input_dialog_title);
        this.title.setText(title);

        this.description = findViewById(R.id.text_input_dialog_description);
        this.description.setText(description);

        this.text = findViewById(R.id.text_input_dialog_text);
        this.text.setText(defaultText);

        this.confirm = findViewById(R.id.text_input_dialog_confirm);
        this.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        show();

    }

    public String getText()
    {

        return text.getText().toString();

    }

}
