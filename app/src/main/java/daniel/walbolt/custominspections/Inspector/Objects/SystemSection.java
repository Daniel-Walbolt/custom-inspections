package daniel.walbolt.custominspections.Inspector.Objects;

import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;

public class SystemSection
{

    /*

    A SystemSection is essentially a unique identifier for every category item.

    They are created for information to be navigated more easily.
        - Photos
        - Saving and loading inspection
        - Comments

     */

    private CategoryItem section;
    private String commentHint;
    private String pdfDescription;
    private String description;

    //When initializing a system section, it requires only the category item.
    public SystemSection(CategoryItem section)
    {

        this.section = section;

    }

    public System getSystem()
    {

        return section.getSystem();

    }

    public void setCommentHint(String commentHint) {
        this.commentHint = commentHint;
    }

    public String getCommentHint() {
        return commentHint;
    }

    public String getPdfDescription() {
        return pdfDescription;
    }

    public void setPdfDescription(String pdfDescription) {
        this.pdfDescription = pdfDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
