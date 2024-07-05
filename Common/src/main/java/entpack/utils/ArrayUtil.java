package entpack.utils;

import com.jfinal.plugin.activerecord.Record;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 数组工具类
 */
public class ArrayUtil {

    /**
     * 刪除集合中的指定索引項
     *
     * @param sourceList 源集合
     * @param removeIdx  索引列表
     * @return 返回刪除指定索引后的集合
     */
    public static List<Record> removeItems(List<Record> sourceList, List<Integer> removeIdx) {
        if (removeIdx.size() > 0) {
            CopyOnWriteArrayList<Record> cowList = new CopyOnWriteArrayList<>(sourceList);
            for (Integer integer : removeIdx) {
                Record target = sourceList.get(integer);
                for (Record item : cowList) {
                    if (item.equals(target)) {
                        cowList.remove(item);
                    }
                }
            }
            return cowList;
        }
        return sourceList;
    }

    /**
     * 从列表中查找一个对象
     *
     * @param list 列表
     * @param key  键名
     * @param val  键值
     * @return 成功返回对象, 否则返回null
     */
    public static Record findOne(List<Record> list, String key, String val) {
        for (Record record : list) {
            if (val.equals(record.getStr(key))) {
                return record;
            }
        }
        return null;
    }

    public static boolean contains(String[] arr, String str) {
        for (String vo : arr) {
            if (vo.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 拆分集合
     *
     * @param <T>
     * @param resList 要拆分的集合
     * @param count   每个集合的元素个数
     * @return 返回拆分后的各个集合
     */
    public static <T> List<List<T>> split(List<T> resList, int count) {
        if (resList == null || count < 1)
            return null;
        List<List<T>> ret = new ArrayList<List<T>>();
        int size = resList.size();
        if (size <= count) {
            //数据量不足count指定的大小
            ret.add(resList);
        } else {
            int pre = size / count;
            int last = size % count;
            //前面pre个集合，每个大小都是count个元素
            for (int i = 0; i < pre; i++) {
                List<T> itemList = new ArrayList<T>();
                for (int j = 0; j < count; j++) {
                    itemList.add(resList.get(i * count + j));
                }
                ret.add(itemList);
            }
            //last的进行处理
            if (last > 0) {
                List<T> itemList = new ArrayList<T>();
                for (int i = 0; i < last; i++) {
                    itemList.add(resList.get(pre * count + i));
                }
                ret.add(itemList);
            }
        }
        return ret;

    }

    /**
     * 复制Record对象相同属性的值
     *
     * @param source 来源对象
     * @param target 目标对象
     */
    public static void recordCopy(Record source, Record target) {
        //使用put的方式
        for (String column : source.getColumnNames()) {
            target.set(column, source.get(column));
        }

		/*//取出来源和目标的相同列
		List<String> sameColumms = new ArrayList<>();
		for (String string1 : source.getColumnNames()) {
			for (String string2 : target.getColumnNames()) {
				if (string1.equals(string2)) {
					sameColumms.add(string1);
				}
			}
		}

		//复制相同列的值
		for (String column : sameColumms) {
			target.set(column, source.get(column));
		}*/
    }

    /**
     * 去除数组中重复元素，并且返回无重复的List
     *
     * @param strings 字符串数组
     * @return 去掉重复元素之后的List
     */
    public static List<String> stringsDistinct(String[] strings) {
        List<String> list = new ArrayList<>(Arrays.asList(strings));

        HashSet<String> h = new HashSet<>(list);
        list.clear();
        list.addAll(h);

        return list;
    }

    /**
     * 去掉list中的重复元素
     *
     * @param strings
     * @return
     */
    public static List<String> stringsDistinct(List<String> strings) {
        HashSet<String> h = new HashSet<>(strings);
        strings.clear();
        strings.addAll(h);

        return strings;
    }

    /**
     * 判断一个数组为null或者空
     *
     * @param lists
     * @return
     */
    public static boolean isEmpty(List<? extends Object> lists) {
        return lists == null || lists.size() == 0;
    }

    /**
     * 判断数组中有数据
     *
     * @param lists
     * @return
     */
    public static boolean notEmpty(List<? extends Object> lists) {
        return lists != null && lists.size() > 0;
    }

    /**
     * 将文件数组转为文件list
     *
     * @param files
     * @return
     */
    public static List<File> getFileListByFiles(File[] files) {
        List<File> list = new ArrayList<>();
        for (File f : files) {
            list.add(f);
        }
        return list;
    }
}