package com.dingxi.xiaoyuantong.network;

import org.json.JSONException;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.dingxi.xiaoyuantong.HomePageActivity;
import com.dingxi.xiaoyuantong.R;
import com.dingxi.xiaoyuantong.HomePageActivity.GetAllNoteTask;

public class ResponseMessage {

	public static int RESULT_TAG_SUCCESS = 0;
	public static int RESULT_TAG_SERVER_ERROR = 1;
	public static int RESULT_TAG_CLIET_ERROR = -1;
	public static final String RESULT_TAG_CODE = "code";
	public static final String RESULT_TAG_MESSAGE = "message";
	public static final String RESULT_TAG_TOTAL = "total";
	public static final String RESULT_TAG_DATAS = "datas";

	public int code = -1;
	public String body;
	public String message;
	public String datas;
	public int total;

	public void praseBody() throws JSONException {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(body)) {

			code = JSONParser
					.getIntByTag(body, ResponseMessage.RESULT_TAG_CODE);
			message = JSONParser.getStringByTag(body,
					ResponseMessage.RESULT_TAG_MESSAGE);
			total = JSONParser.getIntByTag(body, ResponseMessage.RESULT_TAG_TOTAL);
			datas = JSONParser.getStringByTag(body, ResponseMessage.RESULT_TAG_DATAS);

		} 
	}
	
	public void praseBody(Context stringResource) throws JSONException {
        // TODO Auto-generated method stub
        if (!TextUtils.isEmpty(body)) {

            code = JSONParser
                    .getIntByTag(body, ResponseMessage.RESULT_TAG_CODE);
            message = JSONParser.getStringByTag(body,
                    ResponseMessage.RESULT_TAG_MESSAGE);
            total = JSONParser.getIntByTag(body, ResponseMessage.RESULT_TAG_TOTAL);
            datas = JSONParser.getStringByTag(body, ResponseMessage.RESULT_TAG_DATAS);

        } else {
            message =  stringResource.getString(R.string.connection_server_error);
        }
    }
}
