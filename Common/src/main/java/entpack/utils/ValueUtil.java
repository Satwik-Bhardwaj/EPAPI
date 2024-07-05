package entpack.utils;

import com.alibaba.fastjson.JSONObject;

public class ValueUtil {


    /**
     * @param data
     * @param fieldName
     * @return
     */
    public static String getStringVal(JSONObject data, String fieldName) {
        return getStringVal(data, fieldName, null);
    }

    /**
     * @param data
     * @param fieldName
     * @param defVal
     * @return
     */
    public static String getStringVal(JSONObject data, String fieldName, String defVal) {

        if (data == null) {
            return defVal;
        }

        String val = data.getString(fieldName);
        if (val != null) {
            return val.trim();
        }
        return defVal;

//        try {
//            if (data != null) {
//                if (data.get(fieldName) != null) {
//                    if (data.getString(fieldName) != null) {
//                        return data.getString(fieldName).trim();
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return defVal;
    }

    /**
     * @param data
     * @param fieldName
     * @return
     */
    public static Double getDoubleVal(JSONObject data, String fieldName) {
        return getDoubleVal(data, fieldName, null);
    }

    /**
     * @param data
     * @param fieldName
     * @param defVal
     * @return
     */
    public static Double getDoubleVal(JSONObject data, String fieldName, Double defVal) {
        try {
            if (data == null) {
                return defVal;
            }

            Double val = data.getDouble(fieldName);
            if (val != null) {
                return val;
            }

//            if (data != null) {
//                if (data.get(fieldName) != null) {
//                    if (data.getDouble(fieldName) != null) {
//                        return data.getDouble(fieldName);
//                    }
//                }
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return defVal;
    }


    /**
     * @param data
     * @param fieldName
     * @return
     */
    public static Integer getIntVal(JSONObject data, String fieldName) {
        return getIntVal(data, fieldName, null);
    }

    /**
     * @param data
     * @param fieldName
     * @param defVal
     * @return
     */
    public static Integer getIntVal(JSONObject data, String fieldName, Integer defVal) {

        try {
            if (data == null) {
                return defVal;
            }

            Integer val = data.getInteger(fieldName);
            if (val != null) {
                return val;
            }
            if (data != null) {
                if (data.get(fieldName) != null) {
                    if (data.getInteger(fieldName) != null) {
                        return data.getInteger(fieldName);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return defVal;
    }

    public static String getVal(JSONObject data, String fieldName) {
        return getStringVal(data, fieldName, "");
    }
    public static String getVal(JSONObject data, String fieldName, String defVal) {
        return getStringVal(data, fieldName, defVal);
    }

    public static Integer getVal(JSONObject data, String fieldName, Integer defVal) {
        return getIntVal(data, fieldName, defVal);
    }

    public static Double getVal(JSONObject data, String fieldName, Double defVal) {
        return getDoubleVal(data, fieldName, defVal);
    }

}
