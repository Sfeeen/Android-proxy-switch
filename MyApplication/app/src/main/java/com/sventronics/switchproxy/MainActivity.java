package com.sventronics.switchproxy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    EditText ip_input;
    EditText port_input;
    Button on_btn;
    Button off_btn;
    TextView out;
    String command;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ip_input = (EditText)findViewById(R.id.ip_input);
        port_input = (EditText)findViewById(R.id.port);
        on_btn = (Button)findViewById(R.id.proxy_on);
        off_btn = (Button)findViewById(R.id.proxy_off);
        out = (TextView)findViewById(R.id.textView);



    }
    public void on_pressed(View view) {
        String ip = ip_input.getText().toString();
        String port = port_input.getText().toString();
        String command = "settings put global http_proxy " + ip + ":" + port;
        String outp = sudoForResult(command);
        out.setText(out.getText().toString() + "\n" + outp);
    }

    public void off_pressed(View view) {
        String command4 = "settings put global http_proxy :0";

        String outp = sudoForResult(command4);
        out.setText(out.getText().toString() + "\n" + outp);

    }


    public static String sudoForResult(String...strings) {
        String res = "";
        DataOutputStream outputStream = null;
        InputStream response = null;
        try{
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            response = su.getInputStream();

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = readFully(response);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            Closer.closeSilently(outputStream, response);
        }
        return res;
    }

    public static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }


}
