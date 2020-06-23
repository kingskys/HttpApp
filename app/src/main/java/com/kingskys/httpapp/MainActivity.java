package com.kingskys.httpapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.kingskys.http.HttpClient;
import com.kingskys.http.HttpResult;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLPeerUnverifiedException;

public class MainActivity extends AppCompatActivity {

    private TextView resultLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        resultLabel = findViewById(R.id.label_result);

        final EditText urlInput = findViewById(R.id.input_url);
        final EditText dataInput = findViewById(R.id.input_data);


        final TabLayout tabLayout = findViewById(R.id.tabs);
        final View dataLayer = findViewById(R.id.bg_data);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int idx = tab.getPosition();
                if (idx == 0) {
                    dataLayer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int idx = tab.getPosition();
                if (idx == 0) {
                    dataLayer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                if (tab.getText() != null) {
//                    String text = tab.getText().toString();
//                    showResumt("onTabReselected: " + text);
//                }
            }
        });

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultLabel.setText("");
                String url = urlInput.getText().toString().trim();
                if (TextUtils.isEmpty(url)) {
                    Toast.makeText(getApplicationContext(), "没有输入网址", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tabLayout.getSelectedTabPosition() == 0) {
                    String data = dataInput.getText().toString().trim();
                    sendData(url, true, data);
                } else {
                    sendData(url, false, null);
                }
            }
        });

        findViewById(R.id.bg_scene).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
    }

    private void sendData(final String url, final boolean isPost, final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isPost) {
                    try {
                        HttpResult result = new HttpClient().postData(url, data, null);
                        String msg = "post发送结果[" + result.code + "](" + result.success + "): " + result.data;
                        showResumt(msg);
                    } catch (ConnectException e) {
                        showResumt("请检查网络0: " + e);
                    } catch (UnknownHostException e) {
                        showResumt("请检查网络1: " + e);
                    } catch (SSLPeerUnverifiedException e) {
                        showResumt("请检查网址: " + e);
                    } catch (IOException e) {
                        showResumt("post error: " + e);
                    }
                } else {
                    try {
                        HttpResult result = new HttpClient().getData(url);
                        String msg = "get发送结果[" + result.code + "](" + result.success + "): " + result.data;
                        showResumt(msg);
                    } catch (ConnectException e) {
                        showResumt("请检查网络0: " + e);
                    } catch (UnknownHostException e) {
                        showResumt("请检查网络1: " + e);
                    } catch (SSLPeerUnverifiedException e) {
                        showResumt("请检查网址: " + e);
                    } catch (IOException e) {
                        showResumt("get error: " + e);
                    }
                }
            }
        }).start();
    }

    private void showResumt(final String v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultLabel.setText(v);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

}
