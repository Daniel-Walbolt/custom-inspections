package daniel.walbolt.custominspections.PDF;

/*
The Chapter class is heavily used to distinguish the different systems of an inspection report. A chapter contains data about its contents
    such as the page range, number of defects, status (REMOVED, INCOMPLETE, QUALITY, etc.).

The chapter class is basically the title page of any System / subsystem. There are different levels of chapters, similar to how a .docx file has layers of headings.

 */

public class Chapter
{

    private String name;
    private int pageStart;
    private int pageEnd;

    //Chapter will provide a basic overview of the defect inside them
    private int majorDefectCount;
    private int minorDefectCount;

    public Chapter(String systemName)
    {

        this.name = systemName;

    }

    public int getPageStart() {
        return pageStart;
    }

    public int getPageEnd() {
        return pageEnd;
    }

    public int getMajorDefectCount() {
        return majorDefectCount;
    }

    public int getMinorDefectCount() {
        return minorDefectCount;
    }

    public String getChapterName()
    {

        return name;

    }

}
