package entpack.utils;

import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * SQL语句辅助类
 */
public class SQLUtil {

	/**
	 * 生成UPDATE SET部分SQL
	 *
	 * @param record    新实体
	 * @param recordOld 旧实体
	 * @return 返回SET部分SQL, 如无更新则返回空字符串
	 */
	public static String getUpdateSetSql(Record record, Record recordOld) {
		//排除不更新的字段
		List<String> fieldExclude = Arrays.asList("modifyDate", "ver", "creditsBalance");
		StringBuilder sql = new StringBuilder();
		for (String field : record.getColumns().keySet()) {
//			System.out.println(field);
			if (!fieldExclude.contains(field) && (record.get(field) != recordOld.get(field))) {
				String val = record.getStr(field) + "";
				String old = recordOld.getStr(field) + "";
				if (!val.equals(old)) {
//						System.out.println(val);
					if (record.get(field) instanceof String) {
						sql.append(String.format(", %s = '%s'", field, val));
					} else if (record.get(field) instanceof BigDecimal) {
						//判断新旧值是否相等,由于有小数,所以用compareTo而不用equals
						if (new BigDecimal(val).compareTo(new BigDecimal(old)) != 0) {
							sql.append(String.format(", %s = %s", field, val));
						}
					} else {
						sql.append(String.format(", %s = %s", field, val));
					}
				}
			}
		}

		if (sql.length() > 0) {
			sql = new StringBuilder(sql.substring(1, sql.length()));
		}
		return sql.toString();
	}

	/**
	 * 数组转WHERE语句
	 *
	 * @param condition 条件数组
	 * @param op        操作符，AND 或 OR
	 * @return 返回WHERE语句
	 */
	public static String contidionToWhere(List<String> condition, String op) {
		String where = " ";
		if (condition != null && condition.size() > 0) {
			where = " WHERE ";
			for (String str : condition) {
				//用操作符拼接条件
				where += String.format("%s %s", str, op);
			}
			//刪除字符最后的操作符
			where = StringUtil.trimSuffix(where, op);
		}
		return where;
	}

	public static String listToInStr(List<String> list) {
		if (list != null && list.size() > 0) {
			//去重
//			List<String> _list = new ArrayList<>(new HashSet<>(list));
			list.stream().distinct();
			return "('" + StringUtil.listToString(list, "','") + "')";
		}
		return "";
	}

	/**
	 * 根据会员ID计算分表名称
	 *
	 * @param memberId 会员ID
	 * @return 返回分表ID
	 */
	public static String shardingTb(String memberId) {
		return String.format("%03d", Math.abs(getHashCode(memberId) % 127));
	}

	private static long getHashCode(String str) {
		long h = 0;
		if (h == 0) {
			int off = 0;
			char val[] = str.toCharArray();
			long len = str.length();

			for (long i = 0; i < len; i++) {
				h = 31 * h + val[off++];
			}
		}
		return h;
	}
}
