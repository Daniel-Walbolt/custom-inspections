package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.widget.LinearLayout;

import daniel.walbolt.custominspections.Inspector.Objects.System;

public class Observation extends Category{
    public Observation(System parent) {
        super(TYPE.OBSERVATION, parent);
    }

    @Override
    public void load(LinearLayout pageLayout) {

    }
}
