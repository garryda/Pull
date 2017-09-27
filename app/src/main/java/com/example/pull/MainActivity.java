package com.example.pull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
/*
需求一：利用okhttp框架获取到数据
步骤：
1）获取OkhttpClient对象
2）获取Request并且设立访问的资源
3）调用client的newCall(request).execute()方法获取到response对象
4）调用response的body().string()方法获取到返回数据

需求二：利用pull方法解析返回的xml数据
步骤：
1）XmlPullParserFactory.newInstance()获取到XmlPullParserFactory对象
2）调用XmlPullParserFactory对象的newPullParser()方法获取到 XmlPullParser 对象
3）调用 XmlPullParser 对象的setInput(new StringReader(xmlData))方法，其中xmlData是要解析的数据
4）得到int eventType=xmlPullParser.getEventType()，这个eventType为标签名，如果是头标签，其值为
 XmlPullParser.START_TAG，然后获取到其头标签的名 String nodeName = xmlPullParser.getName();进而知道我们获取的是什么的数据，从而
 保存（这边是用已有的数据作为先，因为获取的数据可能为空，如果真的为空会报错），并且处理

 需求三：利用SAX方法解析返回的xml数据
 步骤：
 1）写好继承DefaultHandler的handler对象
 2）获取 SAXParserFactory对象
 3）获取XMLReader对象
 4）将hander的实例设置到xmlreader中
 5）开始执行解析
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt1=(Button)findViewById(R.id.get1);
        Button bt2=(Button)findViewById(R.id.get2);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithOkHttp();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithOkHttp();
            }
        });
    }
    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient();
                    Request request=new Request.Builder()
                            .url("http:/10.0.2.2:8000/get_data.xml")
                            .build();
                    Response response=client.newCall(request).execute();
                    String responseData=response.body().string();
                  // parseXMLWithPull(responseData);
                    parseXMLWithSAX(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void parseXMLWithPull(String xmlData){
        try{
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType=xmlPullParser.getEventType();
            String id="";
            String name="";
            String version="";
            while(eventType!=XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        //开始解析某个节点
                        if ("id".equals(nodeName)) {
                            id = xmlPullParser.nextText();
                        } else if ("name".equals(nodeName)) {
                            name = xmlPullParser.nextText();
                        } else if ("version".equals(nodeName)) {
                            version = xmlPullParser.nextText();
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("app".equals(nodeName)) {
                            Log.d("MainActivity", "id is " + id);
                        }
                        break;

                    default:
                        break;
                }
                eventType = xmlPullParser.next();//跳转下一个标签
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseXMLWithSAX(String xmlData){
        try{
            SAXParserFactory factory=SAXParserFactory.newInstance();
            XMLReader xmlReader=factory.newSAXParser().getXMLReader();
            MyHandler handler=new MyHandler();
            //将hander的实例设置到xmlreader中
            xmlReader.setContentHandler(handler);
            //开始执行解析
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
