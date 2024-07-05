package entpack.serializer;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.redis.serializer.ISerializer;

/**
 * FstSerializer.
 */
public class MyFstSerializer implements ISerializer {

    public static final ISerializer me = new MyFstSerializer();

    public byte[] keyToBytes(String key) {
        return key.getBytes();
    }

    public String keyFromBytes(byte[] bytes) {
        return new String(bytes);
    }

    public byte[] fieldToBytes(Object field) {
        return valueToBytes(field);
    }

    public Object fieldFromBytes(byte[] bytes) {
        return valueFromBytes(bytes);
    }

    public byte[] valueToBytes(Object value) {
        if (value instanceof String) {
            return ((String) value).getBytes();
        }
        return JSONObject.toJSONString(value).getBytes();
    }

    public Object valueFromBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return null;

        return new String(bytes);
    }
}

