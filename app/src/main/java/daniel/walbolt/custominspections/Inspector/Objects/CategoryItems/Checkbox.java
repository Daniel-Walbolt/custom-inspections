package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;

public class Checkbox extends CategoryItem implements InfoItem
{
    public Checkbox(String name, Category category) {
        super(name, category);
    }

    public Checkbox(String name, Category category, long ID) {
        super(name, category,ID);
    }

    /*

    The Checkbox class is the most basic Info-item.

     */

}
