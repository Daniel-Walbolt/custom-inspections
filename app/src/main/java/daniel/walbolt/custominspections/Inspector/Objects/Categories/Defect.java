package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.widget.LinearLayout;

import daniel.walbolt.custominspections.Inspector.Objects.System;

public class Defect extends Category{
    public Defect(System parent) {
        super(TYPE.DEFECT, parent);
    }

    @Override
    public void load(LinearLayout pageLayout) {

    }
}
