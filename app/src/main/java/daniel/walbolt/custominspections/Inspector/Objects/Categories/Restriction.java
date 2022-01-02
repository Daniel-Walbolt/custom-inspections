package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.widget.LinearLayout;

import daniel.walbolt.custominspections.Inspector.Objects.System;

public class Restriction extends Category{

    public Restriction(System parent) {
        super(TYPE.RESTRICTION, parent);
    }

    @Override
    public void load(LinearLayout pageLayout) {

    }
}
