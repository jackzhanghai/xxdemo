package com.dingxi.jackdemo.network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dingxi.jackdemo.model.ClassInfo;
import com.dingxi.jackdemo.model.GradeInfo;
import com.dingxi.jackdemo.model.School;
import com.dingxi.jackdemo.model.StudentInfo;

import android.content.Context;

public class JSONParser {

    public static int jsonToInt(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key))
            return jsonObject.getInt(key);
        return 0;
    }

    public static String jsonToString(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key)) {
            String value = jsonObject.getString(key);
            // jsonObject.isNull(key)没有判断出当natIP为"null"时返回true，所以这里再次判断，如果值为null要返回空字符串
            if (value.equalsIgnoreCase("null") == false) {
                return value;
            }
        }
        return "";
    }

    public static long jsonToLong(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key))
            return jsonObject.getLong(key);
        return 0;
    }

    public static boolean jsonToBoolean(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key))
            return jsonObject.getBoolean(key);
        return false;
    }

    public static JSONArray jsonToArray(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key))
            return jsonObject.getJSONArray(key);
        return new JSONArray();
    }

    public static JSONObject jsonToJSON(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key))
            return jsonObject.getJSONObject(key);
        return null;
    }

//    public static String toParserError(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//        if (!jsonObj.isNull(RestClient.RESULT_TAG_CODE))
//            return jsonObj.getString(HttpRequest.HUB_TAG_MESSAGE);
//        else
//            return jsonObj.getString(HttpRequest.SERVER_TAG_ERRMSG);
//    }
    
    public static int getIntByTag(String json, String tag) throws JSONException {
		JSONObject jsonObj = new JSONObject(json);
		if (jsonObj.has(tag)) {
			return jsonObj.getInt(tag);
		}
		return -1;
	}
    
    public static String getStringByTag(String json, String tag) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        if(jsonObj.has(tag)){
            return jsonObj.getString(tag);
        }
        return null;
    }

    public static ArrayList<School> toParserSchoolList(String json) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        int total = jsonObj.getInt(RestClient.RESULT_TAG_TOTAL);

        ArrayList<School> schoolList = new ArrayList<School>(total);

        if (jsonObj.has(RestClient.RESULT_TAG_DATAS)) {
            JSONArray data = jsonObj.getJSONArray(RestClient.RESULT_TAG_DATAS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);

                School school = new School();

                school.name = obj.getString("name");
                school.id = obj.getString("id");
                schoolList.add(school);
            }
        }
        return schoolList;
    }

    public static ArrayList<StudentInfo> toParserStudentInfoList(String searchTypeReslut) throws JSONException {
        JSONObject jsonObj = new JSONObject(searchTypeReslut);
        int total = jsonObj.getInt(RestClient.RESULT_TAG_TOTAL);

        ArrayList<StudentInfo> schoolList = new ArrayList<StudentInfo>(total);

        if (jsonObj.has(RestClient.RESULT_TAG_DATAS)) {
            JSONArray data = jsonObj.getJSONArray(RestClient.RESULT_TAG_DATAS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);

                StudentInfo studentInfo = new StudentInfo();

                studentInfo.name = obj.getString("stuName");
                studentInfo.id = obj.getString("id");
                schoolList.add(studentInfo);
            }
        }
        return schoolList;
    }

    public static ArrayList<ClassInfo> toParserCalssInfoList(String searchTypeReslut) throws JSONException {
        JSONObject jsonObj = new JSONObject(searchTypeReslut);
        int total = jsonObj.getInt(RestClient.RESULT_TAG_TOTAL);

        ArrayList<ClassInfo> classInfoList = new ArrayList<ClassInfo>(total);

        if (jsonObj.has(RestClient.RESULT_TAG_DATAS)) {
            JSONArray data = jsonObj.getJSONArray(RestClient.RESULT_TAG_DATAS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);

                ClassInfo classInfo = new ClassInfo();

                classInfo.name = obj.getString("name");
                classInfo.id = obj.getString("id");
                classInfoList.add(classInfo);
            }
        }
        return classInfoList;
    }

    public static ArrayList<GradeInfo> toParserGradeInfoList(String searchTypeReslut) throws JSONException {
        JSONObject jsonObj = new JSONObject(searchTypeReslut);
        int total = jsonObj.getInt(RestClient.RESULT_TAG_TOTAL);

        ArrayList<GradeInfo> gradeInfoList = new ArrayList<GradeInfo>(total);

        if (jsonObj.has(RestClient.RESULT_TAG_DATAS)) {
            JSONArray data = jsonObj.getJSONArray(RestClient.RESULT_TAG_DATAS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);

                GradeInfo school = new GradeInfo();

                school.name = obj.getString("name");
                school.id = obj.getString("id");
                gradeInfoList.add(school);
            }
        }
        return gradeInfoList;
    }

//    public static ArrayList<SetFolderBean> toParserImageFileList(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//        int total = jsonObj.getInt("total");
//
//        ArrayList<SetFolderBean> setFolderList = new ArrayList<SetFolderBean>(total);
//
//        if (jsonObj.has("data")) {
//            JSONArray data = jsonObj.getJSONArray("data");
//            for (int i = 0; i < data.length(); i++) {
//                JSONObject obj = (JSONObject) data.get(i);
//                
//                SetFolderBean setFolderBean = new SetFolderBean();
//                setFolderBean.setDir( obj.getString("dir"));
//                setFolderBean.setName( obj.getString("name"));
//                setFolderBean.setPath(obj.getString("path"));
//                setFolderBean.setType(obj.getString("type"));     
//                setFolderBean.setExt(obj.getString("ext"));
//                setFolderBean.setSize(obj.getLong("size"));
//                setFolderBean.setIsadd( obj.getString("isadd"));
//                setFolderBean.setModifyTime(obj.getLong("modifyTime"));
//
//                setFolderList.add(setFolderBean);
//            }
//        }
//        return setFolderList;
//    }
//
//    public static FileContent toParserFileContent(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//
//        FileContent filecontent = new FileContent();
//        filecontent.createTime = jsonObj.getLong("createTime");
//        filecontent.dir = jsonObj.getString("dir");
//        filecontent.name = jsonObj.getString("name");
//        filecontent.path = jsonObj.getString("path");
//        filecontent.type = jsonObj.getString("type");
//        filecontent.visitTime = jsonObj.getLong("visitTime");
//        filecontent.modifyTime = jsonObj.getLong("modifyTime");
//        filecontent.ext = jsonObj.getString("ext");
//        filecontent.size = jsonObj.getLong("size");
//        filecontent.mode = jsonObj.getInt("mode");
//
//        return filecontent;
//    }
//
//    public static FileContent toParserUploadFile(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json).getJSONObject("info");
//
//        FileContent filecontent = new FileContent();
//        filecontent.createTime = jsonObj.getLong("createTime");
//        filecontent.dir = jsonObj.getString("dir");
//        filecontent.name = jsonObj.getString("name");
//        filecontent.path = jsonObj.getString("path");
//        filecontent.type = jsonObj.getString("type");
//        filecontent.visitTime = jsonObj.getLong("visitTime");
//        filecontent.modifyTime = jsonObj.getLong("modifyTime");
//        filecontent.ext = jsonObj.getString("ext");
//        filecontent.size = jsonObj.getLong("size");
//        filecontent.mode = jsonObj.getInt("mode");
//
//        return filecontent;
//    }
//
//    public static int toParseCameraUpload(String json) throws JSONException {
//
//        JSONObject jsonObject = new JSONObject(json);
//
//        int ret_value = jsonObject.getInt("result");
//
//        if (ret_value == 0) {
//            return 0;
//        }
//
//        return ret_value;
//    }
//
//    public static String toParseCameraPath(String json) throws JSONException {
//        JSONObject jsonObject = new JSONObject(json);
//
//        String serverPath = jsonObject.getString("path");
//
//        return serverPath;
//
//    }
//
//    public static String[] toParse(String json) throws JSONException {
//
//        JSONArray jsonArray = new JSONArray();
//
//        return null;
//    }
//
//    public static SearchStatus toParserSearchStatus(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//
//        SearchStatus status = new SearchStatus();
//
//        status.matchCount = jsonObj.getLong("matchCount");
//        status.totalSize = jsonObj.getLong("totalSize");
//        status.folderCount = jsonObj.getLong("folderCount");
//        status.canceled = jsonObj.getInt("canceled");
//        status.finished = jsonObj.getInt("finished");
//        status.startTime = jsonObj.getLong("startTime");
//        status.path = jsonObj.getString("path");
//        status.folder = jsonObj.getString("folder");
//        status.fileCount = jsonObj.getLong("fileCount");
//        status.start = jsonObj.getLong("start");
//        return status;
//    }
//
//    public static ArrayList<SearchResult> toParserSearchResult(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//        ArrayList<SearchResult> searchResultList = new ArrayList<SearchResult>();
//
//        if (jsonObj.has("data")) {
//            JSONArray array = jsonObj.getJSONArray("data");
//
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject obj = (JSONObject) array.get(i);
//
//                SearchResult result = new SearchResult();
//                result.path = obj.getString("path");
//                result.modifyTime = obj.getLong("modifyTime");
//                result.type = obj.getString("type");
//                result.name = obj.getString("name");
//                result.size = obj.getLong("size");
//                result.ext = obj.getString("ext");
//
//                searchResultList.add(result);
//            }
//        }
//
//        return searchResultList;
//    }
//
//    public static String getStringByTag(String json, String tag) throws JSONException {
//        MyLog.d(FileList.class, "json:" + json);
//        JSONObject jsonObj = new JSONObject(json);
//        return jsonObj.getString(tag);
//    }
//
//	public static int getIntByTag(String json, String tag) throws JSONException {
//		JSONObject jsonObj = new JSONObject(json);
//		if (jsonObj.has(tag)) {
//			return jsonObj.getInt(tag);
//		}
//		return -1;
//	}
//
//    /**
//     * 将返回分享的结果解析成sharebean
//     * 
//     * @param json
//     *            分享的结果
//     * @return
//     * @throws JSONException
//     */
//    public static ShareBean toParserShareBean(String json) throws JSONException {
//        JSONObject obj = new JSONObject(json);
//        ShareBean sb = new ShareBean();
//        sb.setAllowpublicaccess(obj.getInt("allowpublicaccess"));
//        sb.setId(obj.getString("id"));
//        sb.setIsdir(obj.getInt("isdir"));
//        sb.setIsvalid(obj.getInt("isvalid"));
//        sb.setLocation(obj.getString("location"));
//        sb.setUrl(obj.getString("url"));
//        sb.setName(obj.getString("name"));
//
//        return sb;
//    }
//
//    public static ArrayList<ShareFile> toParserShareFileList(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//        ArrayList<ShareFile> shareFileList = new ArrayList<ShareFile>();
//
//        if (jsonObj.has("data")) {
//            JSONArray array = jsonObj.getJSONArray("data");
//
//            for (int i = array.length() - 1; i >= 0; i--) {
//                JSONObject obj = (JSONObject) array.get(i);
//
//                ShareFile result = new ShareFile();
//                result.allowpublicaccess = obj.getInt("allowpublicaccess");
//                result.id = obj.getString("id");
//                result.isdir = obj.getInt("isdir");
//                result.isvalid = obj.getInt("isvalid");
//                result.location = obj.getString("location");
//                result.name = obj.getString("name");
//                result.url = obj.getString("url");
//                // result.validityperiod = obj.getInt("validityperiod");
//                result.viewmode = obj.getString("viewmode");
//                result.ext = obj.getString("ext");
//                result.size = obj.getLong("size");
//                result.modifyTime = obj.getLong("modifyTime");
//
//                shareFileList.add(result);
//            }
//        }
//        return shareFileList;
//    }
//
//    private final static Map<Integer, Integer> MonthList = new HashMap<Integer, Integer>() {
//
//        /**
//		 * 
//		 */
//        private static final long serialVersionUID = 1L;
//
//        {
//            put(1, R.string.pictures_january);
//            put(2, R.string.pictures_february);
//            put(3, R.string.pictures_march);
//            put(4, R.string.pictures_april);
//            put(5, R.string.pictures_may);
//            put(6, R.string.pictures_june);
//            put(7, R.string.pictures_july);
//            put(8, R.string.pictures_august);
//            put(9, R.string.pictures_september);
//            put(10, R.string.pictures_october);
//            put(11, R.string.pictures_november);
//            put(12, R.string.pictures_december);
//        }
//    };
//
//    public static ArrayList<LibraryYearGroup> toParserPictureLibraryGroupList(Context cnx,
//            String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//        ArrayList<LibraryYearGroup> libraryGroupList = null;
//
//        if (jsonObj.has("data")) {
//            JSONArray array = jsonObj.getJSONArray("data");
//            int len = array.length();
//            if (len <= 0) {
//                return null;
//            }
//            libraryGroupList = new ArrayList<LibraryYearGroup>();
//            String year = "";
//
//            for (int i = 0; i < len; i++) {
//                JSONObject obj = (JSONObject) array.get(i);
//                String[] title = obj.getString("name").split("-");
//
//                if (year.length() == 0 || title[0].compareToIgnoreCase(year) != 0) {
//                    year = title[0];
//
//                    LibraryYearGroup yeargroup = new LibraryYearGroup();
//                    yeargroup.year = title[0];
//                    yeargroup.grouplist = new ArrayList<LibraryGroup>();
//
//                    libraryGroupList.add(yeargroup);
//                }
//
//                int index = libraryGroupList.size() - 1;
//                LibraryGroup group = new LibraryGroup();
//                group.displayname = cnx.getString(MonthList.get(Integer.parseInt(title[1]))); // 获取中文月份字符串
//                group.name = obj.getString("name");
//                group.count = obj.getInt("count");
//
//                libraryGroupList.get(index).grouplist.add(group);
//            }
//        }
//
//        return libraryGroupList;
//    }
//
//    public static ArrayList<PictureContent> toParserPictureContentList(String json)
//            throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//        ArrayList<PictureContent> pictureContentList = new ArrayList<PictureContent>();
//
//        if (jsonObj.has("data")) {
//            JSONArray array = jsonObj.getJSONArray("data");
//
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject obj = (JSONObject) array.get(i);
//
//                PictureContent picture = new PictureContent();
//                picture.modifyTime = obj.getLong("modifyTime");
//                picture.name = obj.getString("name");
//                picture.path = obj.getString("path");
//                picture.size = obj.getLong("size");
//                picture.ext = obj.getString("ext");
//                picture.hash = obj.getString("hash");
//                pictureContentList.add(picture);
//            }
//        }
//        return pictureContentList;
//    }
//
//    public static String getJSONPath(ArrayList<String> list) throws JSONException {
//        JSONArray array = new JSONArray();
//
//        for (int i = 0; i < list.size(); i++) {
//            array.put(list.get(i));
//        }
//
//        return array.toString();
//    }
//
//    public static Map<String, String> getPathToThumbMap(String path, String filename)
//            throws UnsupportedEncodingException, IOException, JSONException {
//        Map<String, String> map = new HashMap<String, String>();
//
//        // read file
//        // String json = "";
//        StringBuilder json = new StringBuilder();
//        BufferedReader br = new BufferedReader(new FileReader(path + filename));
//        String tmp = "";
//        while (br != null && (tmp = br.readLine()) != null) {
//            // json += tmp;
//            json.append(tmp);
//        }
//        br.close();
//
//        JSONObject obj = new JSONObject(json.toString());
//
//        @SuppressWarnings("unchecked")
//        Iterator<String> keyIter = obj.keys();
//        while (keyIter.hasNext()) {
//            String key = (String) keyIter.next();
//            String value = path + (String) obj.get(key);
//            map.put((String) obj.get(key), value);
//        }
//
//        return map;
//    }
//
//    public static Long toParserDiskSpace(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//        Long diskSpace = jsonObj.getLong("info");
//        return diskSpace;
//    }
//
//    public static ArrayList<Disk> toParserDiskList(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//        ArrayList<Disk> diskList = new ArrayList<Disk>();
//
//        if (jsonObj.has("data")) {
//            JSONArray data = jsonObj.getJSONArray("data");
//            for (int i = 0; i < data.length(); i++) {
//                JSONObject obj = (JSONObject) data.get(i);
//
//                Disk diskContent = new Disk();
//                diskContent.path = obj.getString("path");
//                diskContent.totalSpace = obj.getLong("max");
//                diskContent.freeSpace = (diskContent.totalSpace - obj.getLong("used"));
//                diskList.add(diskContent);
//            }
//        }
//        return diskList;
//    }
//
//    public static ArrayList<String> toParserCameraData(String json) throws JSONException {
//
//        ArrayList<String> data = new ArrayList<String>();
//        // String serverPath = jsonObject.getString("path");
//        JSONObject jsonObject = new JSONObject(json);
//
//        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
//
//        int dataSize = jsonArray.length();
//
//        for (int i = 0; i < dataSize; i++) {
//
//            data.add(jsonArray.getString(i));
//        }
//
//        return data;
//    }
//
//    /**
//     * 主要用来变更hub新框架
//     * 
//     * @param json
//     * @return
//     * @throws JSONException
//     */
//    public static FileServerInfo toParseFileServerInfo(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//
//        FileServerInfo fileserverinfo = new FileServerInfo();
//        fileserverinfo.fileDownloadUrl = jsonObj.getString("fileDownloadUrl");
//        fileserverinfo.fileUploadUrl = jsonObj.getString("fileUploadUrl");
//        fileserverinfo.reqKey = jsonObj.getString("reqKey");
//        fileserverinfo.message = jsonObj.getString("message");
//        fileserverinfo.result = jsonObj.getInt("result");
//
//        return fileserverinfo;
//    }
//
//    public static List<FileContent> toParserAllContactFiles(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//        ArrayList<FileContent> fileList = null;
//
//        if (jsonObj.has("data")) {
//            fileList = new ArrayList<FileContent>();
//            JSONArray data = jsonObj.getJSONArray("data");
//            for (int i = 0; i < data.length(); i++) {
//                JSONObject obj = (JSONObject) data.get(i);
//
//                if (!obj.getString("ext").equalsIgnoreCase("vcf")) {
//                    continue;
//                }
//
//                FileContent filecontent = new FileContent();
//                // filecontent.createTime = obj.getLong("createTime");
//                // filecontent.dir = obj.getString("dir");
//                filecontent.name = obj.getString("name");
//                filecontent.path = obj.getString("path");
//                // filecontent.type = obj.getString("type");
//                // filecontent.visitTime = obj.getLong("visitTime");
//                filecontent.modifyTime = obj.getLong("modifyTime");
//                filecontent.ext = obj.getString("ext");
//                filecontent.size = obj.getLong("size");
//                // filecontent.mode = obj.getInt("mode");
//
//                fileList.add(filecontent);
//            }
//        }
//
//        return fileList;
//    }
//
//    public static OperateFileStatus toParserOperateFileStaue(String response) throws JSONException {
//        // TODO Auto-generated method stub
//        JSONObject jsonObj = new JSONObject(response);
//
//        OperateFileStatus fileStatus = new OperateFileStatus();
//        fileStatus.success = jsonObj.getInt("success");
//        fileStatus.canceled = jsonObj.getInt("canceled");
//        fileStatus.finishedPath = jsonObj.getString("finishedPath");
//        fileStatus.totalCount = jsonObj.getInt("totalCount");
//        fileStatus.finished = jsonObj.getInt("finished");
//        fileStatus.operatingFileName = jsonObj.getString("operating");
//        fileStatus.result = jsonObj.getInt("result");
//        fileStatus.operated = jsonObj.getInt("operated");
//
//        return fileStatus;
//    }
}
