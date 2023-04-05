package mg.x261.SubmissionReport;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import mg.x261.SubmissionReport.R;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<Report> mReportList;
    private List<Report> mReportListFileterd;

    public ReportAdapter(List<Report> list) {
        mReportList = list;
        mReportListFileterd = list;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        holder.bind(mReportListFileterd.get(position));
    }

    @Override
    public int getItemCount() {
        return mReportListFileterd.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Report> reportList) {
        mReportList = reportList;
        mReportListFileterd = reportList;
        notifyDataSetChanged();
    }

    public void setReportList(List<Report> reportList) {
        mReportList = reportList;
        mReportListFileterd = reportList;
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                if (query.isEmpty()) {
                    mReportListFileterd = mReportList;
                } else {
                    List<Report> temp = new ArrayList<>();
                    for (Report item : mReportList) {
                        if (item.getmStatus().toLowerCase().contains(query)) {
                            temp.add(item);
                        }
                    }
                    mReportListFileterd = temp;
                }
                results.values = mReportListFileterd;
                results.count = mReportListFileterd.size();
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mReportListFileterd = (ArrayList<Report>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {

        private TextView mNameTextView;
        private TextView mIdTextView;
        private TextView mSizeTextView;
        private TextView mStatusTextView;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.nameTextView);
            mIdTextView = itemView.findViewById(R.id.idTextView);
            mSizeTextView = itemView.findViewById(R.id.sizeTextView);
            mStatusTextView = itemView.findViewById(R.id.statusTextView);

        }
        @SuppressLint("SetTextI18n")
        public void bind(Report report) {
            mNameTextView.setText(report.getmName());
            mIdTextView.setText(report.getmId());
            mSizeTextView.setText("Size: " + report.getmSize());
            mStatusTextView.setText("Status: " + report.getmStatus());
        }

    }

}
