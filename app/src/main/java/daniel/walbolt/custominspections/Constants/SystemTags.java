package daniel.walbolt.custominspections.Constants;

import android.content.Context;

import androidx.core.content.ContextCompat;

import daniel.walbolt.custominspections.R;

public enum SystemTags
{

    INCOMPLETE {
        @Override
        public int getTagColor(Context context) {
            return ContextCompat.getColor(context, R.color.system_incomplete);
        }

        @Override
        public String getPDFString() {
            return "Incomplete Inspection";
        }
    },
    COMPLETE {
        @Override
        public int getTagColor(Context context) {
            return ContextCompat.getColor(context, R.color.system_complete);
        }

        @Override
        public String getPDFString() {
            return "Complete";
        }
    },
    QUALITY {
        @Override
        public int getTagColor(Context context) {
            return ContextCompat.getColor(context, R.color.system_quality);
        }

        @Override
        public String getPDFString() {
            return "Quality of Residence";
        }
    },
    EXCLUDED {
        @Override
        public int getTagColor(Context context) {
            return ContextCompat.getColor(context, R.color.system_excluded);
        }

        @Override
        public String getPDFString() {
            return "Excluded from Report";
        }
    },
    PARTIAL {
        @Override
        public int getTagColor(Context context) {
            return ContextCompat.getColor(context, R.color.system_partial);
        }

        @Override
        public String getPDFString() {
            return "Partially Inspected";
        }
    },

    ;

    public abstract int getTagColor(Context context);
    public abstract String getPDFString();

}
