package mg.x261.SubmissionReport;

import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class ActivityAssignReport extends AppCompatActivity {


    private Spinner mSpinner;
    private ArrayAdapter<String> mSpinnerAdapter;
    private RecyclerView mRecyclerView;
    private ReportAdapter mReportAdapter;
    private TextView lastUpdateTextView;
    private Toolbar toolbar;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<mg.x261.SubmissionReport.List> assignments = new ArrayList<>();
    private List<Report> reports = new ArrayList<>();
    private NetWorkUtils netWorkUtils;

    String mJsonString1 =
            "{\"reports\":[{\"name\":\"XueXiang\",\"id\":\"20205911\",\"size\":\"0.2Ko\",\"status\":\"Processing\\n\"},{\"name\":\"\\u6731\\u5f00\\u6e90\",\"id\":\"20204229\",\"size\":\"0.2 12Ko\",\"status\":\"Processing\\n\"},{\"name\":\"\\u8c22\\u5b9d\\u6770\",\"id\":\"20201703\",\"size\":\"0.2 Ko\",\"status\":\"Processing\\n\"}]}";
    String mJsonString2 =
            "{\"reports\":[{\"name\":\"ZhuZiJun\",\"id\":\"20204051\",\"size\":\"13.1Mo\",\"status\":\"Processing\\n\"},{\"name\":\"\\u4efb\\u799bAzil\",\"id\":\"20201697\",\"size\":\"13.2 Mo\",\"status\":\"Processing\\n\"},{\"name\":\"\\u6731\\u5f00\\u6e90\",\"id\" :\"20204229\",\"size\":\"13 Mo\",\"status\":\"Processing\\n\"}]}";
    String assignUrl = "https://studio.mg/submission2023/api-assignment.php";
    String reportUrl = "https://studio.mg/submission2023/api-report.php";
    String apiKey = "89821d232c6a62c57c369a9c8372fbc52bd9e206233748fb4032f86d28c2e86d";
    String assignIdUrl = "assign_001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//关联activity.xml
        // 关联用户名、密码和登录、注册按钮
        EditText userName = (EditText) this.findViewById(R.id.UserNameEdit);
        EditText passWord = (EditText) this.findViewById(R.id.PassWordEdit);
        Button loginButton = (Button) this.findViewById(R.id.LoginButton);
        Button signUpButton = (Button) this.findViewById(R.id.SignUpButton);
        // 登录按钮监听器
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strUserName = userName.getText().toString().trim();
                        String strPassWord = passWord.getText().toString().trim();
                        // 判断如果用户名是123456 密码是123456就是登录成功
                        if (strUserName.equals("123456") && strPassWord.equals("123456")) {
                            Toast.makeText(ActivityAssignReport.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ActivityAssignReport.this, "请输入正确的用户名或密码！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        // 注册按钮监听器
        signUpButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ActivityAssignReport.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                }
        );
        setTitle("Assign Report");

        netWorkUtils = new NetWorkUtils(this);
        if (netWorkUtils.isNetworkConnected()) {
            loadAssignData(assignUrl);
            loadReportData(reportUrl, apiKey, assignIdUrl);
        }

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                assignIdUrl = selectedItem.trim().toLowerCase().replace(" ", "_");
                if (netWorkUtils.isNetworkConnected()) {
                    loadReportData(reportUrl, apiKey, assignIdUrl);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please check the internet connection", Toast.LENGTH_SHORT).show();
                }

                searchView.setQuery("", false);
                updateRecyclerView(reports);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (netWorkUtils.isNetworkConnected()) {
                String freshAssignIdUrl = assignIdUrl;
                String freshProcessedId;

                if (assignments.isEmpty()) {
                    loadAssignData(assignUrl);
                    loadReportData(reportUrl, apiKey, assignIdUrl);
                } else {
                    freshAssignIdUrl = mSpinner.getSelectedItem().toString();
                    freshProcessedId = freshAssignIdUrl.trim().toLowerCase().replace(" ", "_");
                    loadAssignData(assignUrl);
                    loadReportData(reportUrl, apiKey, freshProcessedId);
                }

                searchView.setQuery("", false);
                updateRecyclerView(reports);
                Toast.makeText(getApplicationContext(), "Refresh " + freshAssignIdUrl + " succeeded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Please check the internet connection", Toast.LENGTH_SHORT).show();
            }

            swipeRefreshLayout.setRefreshing(false);

        });


    }

    private void initView() {

        mSpinner = findViewById(R.id.sourceSelectionSpinner);
        mRecyclerView = findViewById(R.id.reportRecyclerView);
        lastUpdateTextView = findViewById(R.id.lastUpdateTextView);
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Assignment Tracker Pro");
        mReportAdapter = new ReportAdapter(reports);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mReportAdapter);
        mSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
    }

    private void loadAssignData(String url) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        String aName, aId;
                        JSONArray jsonArray = response.getJSONArray("assignments");
                        assignments = parseAssignJSONArray(jsonArray);
                        mSpinnerAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Error when downloading data",
                            Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "Error when downloading data:" + error);

                });

        requestQueue.add(jsonObjectRequest);
    }

    private void loadReportData(String url, String api, String assignUrl) {
        url = url + "?apikey=" + api + "&q=" + assignUrl;
        Log.d(TAG, "loadReportData from: " + url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        String mName, mId, mSize, mStatus;
                        JSONArray jsonArray = response.getJSONArray("reports");
                        reports = parseReportJSONArray(jsonArray);
                        mReportAdapter.updateData(reports);
                    } catch (JSONException e) {
                        Snackbar.make(findViewById(android.R.id.content),
                                "There was an error loading the data. Please try again later.", Snackbar.LENGTH_LONG).show();
                        Log.e("JSON", "Error parsing JSON data", e);
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Error when downloading data",
                            Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "Error when downloading data:" + error);

                });


        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mReportAdapter.getFilter().filter(newText);

                return false;
            }
        });

        return true;
    }


    private List<Report> parseReportJSONArray(JSONArray jsonArray) {
        List<Report> reportList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject reportObject = jsonArray.getJSONObject(i);
                String name = reportObject.getString("name");
                String id = reportObject.getString("id");
                String size = reportObject.getString("size");
                String status =
                        removeLeadingAndTrailingNewLines(reportObject.getString("status"));
                reportList.add(new Report(name, id, size, status));
            }
        } catch (JSONException e) {

            Snackbar.make(findViewById(android.R.id.content),
                    "There was an error loading the data. Please try again later.", Snackbar.LENGTH_LONG).show();
            e.printStackTrace();
            Log.e("JSON", "Error parsing JSON data", e);
        }
        return reportList;
    }

    public List<mg.x261.SubmissionReport.List> parseAssignJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<mg.x261.SubmissionReport.List> assignments = new ArrayList<>();
        String aId, aName;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            aId = jsonObject.optString("id", null);
            aName = jsonObject.optString("name", null);
            mg.x261.SubmissionReport.List tempAssign = new mg.x261.SubmissionReport.List(aId, aName);
            assignments.add(tempAssign);
            mSpinnerAdapter.add(tempAssign.getName() + " " + tempAssign.getId());
        }

        return assignments;
    }

    private void updateRecyclerView(List<Report> reportList) {
        mReportAdapter.updateData(reportList);
        setLastUpdateDate();
    }

    @SuppressLint("SetTextI18n")
    private void setLastUpdateDate() {
        TextView lastUpdateTextView = findViewById(R.id.lastUpdateTextView);
        TimeZone time = TimeZone.getTimeZone("Etc/GMT-8");
        TimeZone.setDefault(time);
        String currentDateTimeString = new Date().toString();
        lastUpdateTextView.setText("Last update: " + currentDateTimeString);
    }

    private String removeLeadingAndTrailingNewLines(String str) {
        return str == null || str.isEmpty() ? str :
                str.replaceAll("(^\\n+|\\n+$)", "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        netWorkUtils.unregisterNetworkCallback(this);
    }
}
