package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.widget.LinearLayout;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;

public class Checkbox extends CategoryItem
{

    public Checkbox(String name, Category category) {
        super(name, category);
    }

    @Override
    public void onChecked() {
        //TODO
    }

    @Override
    public void load(LinearLayout categoryLayout) {

    }
}
