/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dingxi.jackdemo;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.xmlpull.v1.XmlPullParserException;

import com.dingxi.jackdemo.network.RestClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link LocalServiceActivity.Controller}
 * and {@link LocalServiceActivity.Binding} classes show how to interact with the
 * service.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

public class LocalService extends Service {


    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = 1234;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }
    
    @Override
    public void onCreate() {

       
        
        int lastTime;
        
        do {
            
            String schoolsInfo = null;
            try {
                
                
                schoolsInfo = RestClient.getAllChilds("", "", "");
            } catch (ConnectTimeoutException stex) {
                schoolsInfo = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                schoolsInfo = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                schoolsInfo = getString(R.string.connection_server_error);
            } catch (XmlPullParserException e) {
                schoolsInfo = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (IOException e) {
                schoolsInfo = getString(R.string.connection_error);
                e.printStackTrace();
            }
            
            
        } while (true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        
        intent.getStringExtra("");
        
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
 
}

