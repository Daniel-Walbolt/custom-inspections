package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;

public class Numeric extends CategoryItem
{

    private String text;

    public Numeric(String name, Category category)
    {
        super(name, category);
        text = ""; // Default value of the text input

    }

    public void setText(String numericText)
    {

        this.text = numericText;

    }

    public String getText()
    {

        return text;

    }

}
