package entpack.tasks;

import com.google.common.util.concurrent.RateLimiter;
import com.jfinal.plugin.activerecord.Db;
import entpack.utils.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DelDataTask implements Job {

	private static Logger logger = LoggerFactory.getLogger("delDataTask");
	private static AtomicInteger doItemCount = new AtomicInteger(0);
	private static final int len = 1;

	/**
	 * 以1r/s往桶中放入令牌
	 */
	private static RateLimiter limiter = RateLimiter.create(1);

	private static final String[] delSqlTemplates = (
//            "delete from api_tgp_debit where senton<'2020-03-01T00:00:00+07:00' \n" +
//            "delete from api_tgp_log where senton<'2020-03-01T00:00:00+07:00'\n" +
//            "delete from api_tgp_ticket where beton<'2020-03-01T00:00:00+07:00'\n" +
//            "delete from api_tgp_credit_new where timestamp<'2020-03-01T00:00:00+07:00' \n" +
//            "delete from api_tgp_cancel where timestamp<'2020-03-01T00:00:00+07:00'\n" +
//            "delete from api_online_games_bet where createTime<'2020-03-01 00:00:00'\n" +
//            "delete from api_online_games_bet_log where createTime<'2020-03-01 00:00:00'\n" +
//            "delete from api_online_games_ticket where txTime<'2020-03-01T00:00:00+07:00'\n" +
			"delete from api_sa_bet where createTime< TIMESTAMP '2020-03-01 00:00:00'\n" +
					"delete from api_sa_bet_log where createTime< TIMESTAMP '2020-03-01 00:00:00'\n" +
					"delete from api_sa_betDetails where betTime< TIMESTAMP '2020-03-01 00:00:00'\n" +
					"delete from api_sa_betDetails_1030 where betTime< TIMESTAMP '2020-03-01 00:00:00'\n" +
					"delete from api_sa_userBetDetails where betTime< TIMESTAMP '2020-03-01 00:00:00'\n" +
					"delete from api_sa2_bet where createTime< TIMESTAMP '2020-03-01 00:00:00'\n" +
					"delete from api_sa2_bet_log where createTime< TIMESTAMP '2020-03-01 00:00:00'\n" +
					"delete from api_sa2_betDetails where betTime< TIMESTAMP '2020-03-01 00:00:00'\n" +
					"delete from api_sa2_betDetails_1030 where betTime< TIMESTAMP '2020-03-01 00:00:00'\n" +
					"delete from api_sa2_userBetDetails where betTime< TIMESTAMP '2020-03-01 00:00:00'\n" +
					"DELETE FROM entpackHistory.master where createDate < TIMESTAMP '2020-03-01 00:00:00'\n" +
					"DELETE FROM entpackHistory.agent where createDate < TIMESTAMP '2020-03-01 00:00:00'\n" +
					"DELETE FROM entpackHistory.member where createDate < TIMESTAMP '2020-03-01 00:00:00'\n" +
					"DELETE FROM entpackHistory.company where createDate < TIMESTAMP '2020-03-01 00:00:00'\n" +
					"DELETE FROM entpackTicket.`ticket`where createDate < TIMESTAMP '2020-03-01 00:00:00'\n" +

					"DELETE FROM entpack.tb_member_balance_log where createDate < TIMESTAMP '2020-03-01 00:00:00'\n" +
//					"DELETE FROM entpack.tb_member_balance where createDate < TIMESTAMP '2020-03-01 00:00:00'\n" +
//					"DELETE FROM entpack.tb_master_balance where createDate < TIMESTAMP '2020-03-01 00:00:00'\n" +
//					"DELETE FROM entpack.tb_agent_balance where createDate < TIMESTAMP '2020-03-01 00:00:00'\n" +
//					"DELETE FROM entpack.tb_report_master where date < TIMESTAMP '2020-03-01 00:00:00'\n" +
//					"DELETE FROM entpack.tb_report_member where date < TIMESTAMP '2020-03-01 00:00:00'\n" +
//					"DELETE FROM entpack.tb_report_agent where date < TIMESTAMP '2020-03-01 00:00:00'\n" +
//					"DELETE FROM entpack.tb_report_company where date < TIMESTAMP '2020-03-01 00:00:00'\n" +
					"")
			.split("\n");

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			//单线程模式
			if (doItemCount.get() < len) {
				doItemCount.incrementAndGet();
				doItem();
				doItemCount.decrementAndGet();
			}
		} catch (Exception ex) {
			doItemCount.decrementAndGet();
			logger.error("DelDataTask.error", ex.fillInStackTrace());
			ex.printStackTrace();
		}
	}

	public static void doItem() {
		boolean allFinished = false;
		while (!allFinished) {

			while (limiter.tryAcquire()) {

				// 3个月前
				Date date = DateUtil.addHour(new Date(), Calendar.MONTH, -3);
				String toDay = DateUtil.formatDate(date, "yyyy-MM-dd");

				Map<String, Boolean> hFinished = new HashMap<>();

				for (String delSqlTemplate : delSqlTemplates) {

					String delSql = delSqlTemplate.replace("2020-03-01", toDay) + " limit 500";

					if (hFinished.containsKey(delSql)) {
						continue;
					}

					long startTime = System.currentTimeMillis();
					int rows = Db.update(delSql);
					if (rows == 0) {
						hFinished.put(delSql, true);
					}
					long endTime = System.currentTimeMillis();
					logger.info("{} {}ms,count:{}", "sql:" + delSql, (endTime - startTime), rows);

					long hour = DateUtils.getFragmentInHours(new Date(), Calendar.DATE);
					if (hour > 8) {
						//上午8点了，停止删除 数据
						allFinished = true;
						break;
					}
				}
				if (delSqlTemplates.length == hFinished.size()) {
					allFinished = true;
					break;
				}
			}
		}
	}
}
