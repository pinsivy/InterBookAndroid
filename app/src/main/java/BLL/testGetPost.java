package BLL;

/**
 * Created by dell on 14/06/2015.
 */
public class testGetPost {

    /*public class MainActivity extends Activity {

        TextView output;
        TextView output2;
        ProgressBar pb;
        List<MyTask> tasks;

        List<Departement> departementList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

//		Initialize the TextView for vertical scrolling
            output = (TextView) findViewById(R.id.textView2);
            output.setMovementMethod(new ScrollingMovementMethod());

            output2 = (TextView) findViewById(R.id.textView);

            pb = (ProgressBar) findViewById(R.id.progressBar1);
            pb.setVisibility(View.INVISIBLE);

            tasks = new ArrayList<>();
        }

        protected void updateDisplay() {
            if (departementList!=null) {
                for (Departement flower : departementList) {
                    output.append(flower.getName() + "\n");
                }
            }
        }

        protected boolean isOnline() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            } else {
                return false;
            }
        }

        private class MyTask extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                //updateDisplay();

                if (tasks.size() == 0) {
                    pb.setVisibility(View.VISIBLE);
                }
                tasks.add(this);
            }

            @Override
            protected String doInBackground(String... params) {
                String content = "";
                if (params[0] == "get")
                    content = HttpManager.getData(params[1]);
                else if (params[0] == "post")
                    content = HttpManager.postData(params[1], params[2]);
                return content;
            }

            @Override
            protected void onPostExecute(String result) {

                if (result != null) {

                    List<List<String>> genericList = XMLParser.parseFeed("Ref_DepartementSimple", new String[]{"id_Departement", "Description", "id_Region"}, result);
                    departementList = new ArrayList<>();

                    for(List<String> lo : genericList) {
                        departementList.add(new Departement(lo.get(0),lo.get(1)));
                    }

                    updateDisplay();

                    tasks.remove(this);
                    if (tasks.size() == 0) {
                        pb.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            protected void onProgressUpdate(String... values) {
                //updateDisplay(values[0]);
            }

        }

        public void BoutonSeConnecter(View v){
            if (isOnline()) {
                MyTask task = new MyTask();
                task.execute("get", "http://interbook-dev.com/BLL/IBWS.asmx/GetDepartements");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }

        public void BoutonDelete(View v){
            if (isOnline()) {
                MyTask task = new MyTask();
                task.execute("post", "http://interbook-dev.com/BLL/IBWS.asmx/deleteLine_Reservation", "id_Reservation=65");
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }
    }*/


}
