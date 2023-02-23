// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.intellij.sdk.toolWindow;

import com.intellij.openapi.wm.ToolWindow;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.swing.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class MyToolWindow {
  private JPanel myToolWindowContent;
  private JTextField urlText;
  private JButton sendButton;
  private JComboBox apiCallType;
  private JTextArea postBodyText;
  private JTextArea responseBody;
  CloseableHttpClient httpClient = HttpClients.createDefault();

  public MyToolWindow(ToolWindow toolWindow) {
    apiCallType.addItem(new ComboItem("GET", "GET"));
    apiCallType.addItem(new ComboItem("POST", "POST"));
    apiCallType.addActionListener(e -> clearPostBodyData());
    sendButton.addActionListener(e -> performApiCall());
  }


  private void clearPostBodyData() {
    postBodyText.setText("");
    responseBody.setText("");
  }

  private void performApiCall() {
    System.out.println(apiCallType);
    String url = urlText.getText();
    ComboItem selectedType = (ComboItem) apiCallType.getItemAt(apiCallType.getSelectedIndex());

    if(url != null && url.length() > 0) {
      if("GET".equals(selectedType.getValue())) {
        HttpGet getCall = new HttpGet(url);
        try {
          CloseableHttpResponse response = httpClient.execute(getCall);
          HttpEntity entity = response.getEntity();
          if (entity != null) {
            String result = EntityUtils.toString(entity);
            System.out.println(result);
            responseBody.setText(result);
            response.close();
          }
        } catch (IOException e) {
          responseBody.setText(e.getMessage());
          e.printStackTrace();
        }
      } else if ("POST".equals(selectedType.getValue())) {
        String postBody = postBodyText.getText();
        HttpPost postCall = new HttpPost(url);
        try {
          HttpEntity reqentity = new ByteArrayEntity(postBody.getBytes("UTF-8"));
          postCall.setEntity(reqentity);
          try {
            CloseableHttpResponse response = httpClient.execute(postCall);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
              String result = EntityUtils.toString(entity);
              System.out.println(result);
              responseBody.setText(result);
              response.close();
            }
          } catch (IOException e) {
            responseBody.setText(e.getMessage());
            e.printStackTrace();
          }
        } catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
      }
    } else {
      responseBody.setText("No response! no url provided.");
    }

  }

  public JPanel getContent() {
    return myToolWindowContent;
  }

}
