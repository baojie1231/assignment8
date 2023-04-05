package mg.x261.SubmissionReport;

public class Report {

    private String mName;
    private String mId;
    private String mSize;
    private String mStatus;

    Report(String name, String id, String size, String status) {
        this.mName = name;
        this.mId = id;
        this.mSize = size;
        this.mStatus = status;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmSize() {
        return mSize;
    }

    public void setmSize(String mSize) {
        this.mSize = mSize;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }
}
