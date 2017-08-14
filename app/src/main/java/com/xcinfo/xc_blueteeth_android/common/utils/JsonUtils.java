package com.xcinfo.xc_blueteeth_android.common.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p/>
 * 描述:
 * <p/>
 * 作者:Administrator
 * <p/>
 * 时间:2016/3/9 16:20
 * <p/>
 * 版本:
 */
public class JsonUtils {
    private JSONObject jsonObject;

    public JsonUtils(String str) throws JSONException {
        jsonObject = new JSONObject(str);
    }

    public String toString() {
        return jsonObject.toString();
    }

    public JsonUtils getJSONUtils(String str) {
        try {
            if (jsonObject != null && jsonObject.has(str) && jsonObject.get(str) != null && !StringUtils.isEmpty(jsonObject.get(str).toString())) {
                return new JsonUtils(jsonObject.getString(str));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void init(String jsonData) {
        try {
            jsonObject = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean getBoolean(String str) {
        try {
            if (jsonObject != null && jsonObject.has(str) && jsonObject.get(str) != null && !StringUtils.isEmpty(jsonObject.get(str).toString())) {
                return jsonObject.getBoolean(str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getDouble(String str) {
        try {
            if (jsonObject != null && jsonObject.has(str) && jsonObject.get(str) != null && !StringUtils.isEmpty(jsonObject.get(str).toString())) {
                return jsonObject.getDouble(str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int getInt(String str) {
        try {
            if (jsonObject != null && jsonObject.has(str) && jsonObject.get(str) != null && !StringUtils.isEmpty(jsonObject.get(str).toString())) {
                return jsonObject.getInt(str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public JSONArray getJSONArray(String str) {
        try {
            if (jsonObject != null && jsonObject.has(str) && jsonObject.get(str) != null && !StringUtils.isEmpty(jsonObject.get(str).toString())) {
                return jsonObject.getJSONArray(str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getJSONObject(String str) {
        try {
            if (jsonObject != null && jsonObject.has(str) && jsonObject.get(str) != null && !StringUtils.isEmpty(jsonObject.get(str).toString())) {
                return jsonObject.getJSONObject(str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getLong(String str) {
        try {
            if (jsonObject != null && jsonObject.has(str) && jsonObject.get(str) != null && !StringUtils.isEmpty(jsonObject.get(str).toString())) {
                return jsonObject.getLong(str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getString(String str) {
        try {
            if (jsonObject != null && jsonObject.has(str) && jsonObject.get(str) != null && !StringUtils.isEmpty(jsonObject.get(str).toString())) {
                return jsonObject.getString(str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
