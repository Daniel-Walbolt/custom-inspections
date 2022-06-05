package daniel.walbolt.custominspections.Inspector.Objects.Other;

public interface MajorComponent
{

    boolean getCompletionStatus(); // Get the status of this component. If it is FALSE, the report can not be created, and the inspection can not be finished.

    String getComponentDescription(); // Returns a phrase that describes where this item is located

}
