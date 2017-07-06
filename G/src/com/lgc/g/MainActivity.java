package com.lgc.g;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	public static final int SHOW_RESPONSE = 0;
	private String url;
	
	private static TextView namePrice;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
        setContentView(R.layout.activity_main);
        
        namePrice = (TextView)findViewById(R.id.name_price);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	// 新建Handler的对象，在这里接收Message，然后更新TextView控件的内容
	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_RESPONSE:
				String response = (String) msg.obj;				 
				String result[] = response.split("~");
				if (result.length > 3) {
					namePrice.setText(result[1] + " " + result[3]);
				} else {
					namePrice.setText("代码错误");
				}
				break;
			default:
				break;
			}
		}
	};
    
    public void onClick_query(View view) {
    	EditText stockCode = (EditText)findViewById(R.id.editText_stockCode);
//        Log.d("stockCode: ", stockCode.getText().toString()); 
    	url = Integer.parseInt(stockCode.getText().toString()) > 600000 ? 
    			"http://qt.gtimg.cn/q=" + "sh" + stockCode.getText().toString() : 
    				"http://qt.gtimg.cn/q=" + "sz" + stockCode.getText().toString();
    	sendRequestWithHttpClient();
    }
    
	// 方法：发送网络请求，获取百度首页的数据。在里面开启线程
	private void sendRequestWithHttpClient() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 用HttpClient发送请求，分为五步
				// 第一步：创建HttpClient对象
				HttpClient httpCient = new DefaultHttpClient();
				// 第二步：创建代表请求的对象,参数是访问的服务器地址
				HttpGet httpGet = new HttpGet(url); //"http://qt.gtimg.cn/q=sh600340"

				try {
					// 第三步：执行请求，获取服务器发还的相应对象
					HttpResponse httpResponse = httpCient.execute(httpGet);
					// 第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// 第五步：从相应对象当中取出数据，放到entity当中
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");// 将entity当中的数据转换为字符串

						// 在子线程中将Message对象发出去
						Message message = new Message();
						message.what = SHOW_RESPONSE;
						message.obj = response.toString();
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();// 这个start()方法不要忘记了
	}
}
