package com.dywl.familybed.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WebTool {

    //主要用于传输单条数据，如字符串等，同时，PHP端也返回单条数据
    public static String singleData(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            byte[] data = StreamTool.read(inputStream);
            String result = new String(data);
            return result;
        }
        return "failed";
    }

    //用于获取多种复杂类型的数据
    public static ArrayList<MultipleResult> multipleData(String n) throws Exception {
        ArrayList<MultipleResult> list=new ArrayList<>();
        String path="https://www.recycle11.top/Connect_Android/get_multiple_data.php?n="+n;
        HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
        InputStream json = conn.getInputStream();
        byte[] data = StreamTool.read(json);
        String json_str = new String(data);
        JSONArray jsonArray = new JSONArray(json_str);
        for(int i = 0; i < jsonArray.length() ; i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String a1=jsonObject.getString("a1");
            int a2=jsonObject.getInt("a2");
            double a3=jsonObject.getDouble("a3");
            list.add(new MultipleResult(a1,a2,a3));
        }
        return list;
    }

    public static void uploadFile(String path, String url_path, String name) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try
        {
            URL url = new URL(url_path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
          /* Output to the connection. Default is false,
             set to true because post method must write something to the connection */
            con.setDoOutput(true);
            /* Read from the connection. Default is true.*/
            con.setDoInput(true);
            /* Post cannot use caches */
            con.setUseCaches(false);
            /* Set the post method. Default is GET*/
            con.setRequestMethod("POST");
            /* 设置请求属性 */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            /*设置StrictMode 否则HTTPURLConnection连接失败，因为这是在主进程中进行网络连接*/
            // StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            /* 设置DataOutputStream，getOutputStream中默认调用connect()*/
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());  //output to the connection
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " +
                    "name=\"file\";filename=\"" +
                    name+"\"" + end);
            ds.writeBytes(end);
            /* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(path);
            /* 设置每次写入8192bytes */
            int bufferSize = 8192;
            byte[] buffer = new byte[bufferSize];   //8k
            int length = -1;
            /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1)
            {
                /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* 关闭流，写入的东西自动生成Http正文*/
            fStream.close();
            /* 关闭DataOutputStream */
            ds.close();
            /* 从返回的输入流读取响应信息 */
            InputStream is = con.getInputStream();  //input from the connection 正式建立HTTP连接
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1)
            {
                b.append((char) ch);
            }
            /* 显示网页响应内容 */
            System.out.println(b.toString().trim());
            //Toast.makeText(MainActivity.this, b.toString().trim(), Toast.LENGTH_SHORT).show();//Post成功
        } catch (Exception e)
        {
            /* 显示异常信息 */
            System.out.println("fail"+e);
            //Toast.makeText(MainActivity.this, "Fail:" + e, Toast.LENGTH_SHORT).show();//Post失败
        }
    }

    public static class StreamTool {
        //从流中读取数据
        public static byte[] read(InputStream inStream) throws Exception{
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = inStream.read(buffer)) != -1)
            {
                outStream.write(buffer,0,len);
            }
            inStream.close();
            return outStream.toByteArray();
        }
    }

}
