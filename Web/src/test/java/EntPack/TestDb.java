package EntPack;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TestDb extends TestBase {

	@Test
	public void loan15() {
		Instant start = Instant.now();
		List<Record> list = new ArrayList<>();
		int num = 0;
		for (int loan_amount = 1000; loan_amount <= 1000000; loan_amount = loan_amount + 1000) {
			for (int interest_rate = 18; interest_rate <= 48; interest_rate++) {
				for (int installment = 1; installment <= 60; installment++) {
					list.add(new Record().set("loan_amount", loan_amount)
							.set("interest_rate", interest_rate)
							.set("installment", installment));
					num++;
				}
			}
		}
		Db.batchSave("interest_rate_2", list, 30000);

		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis();
		System.out.println("耗時: " + timeElapsed);
		System.out.println(Ret.ok("SUCCESS, 行数 : " + num + ", 实际写入行数 : " + Db.queryInt("select count(1) from interest_rate_2") + ", 耗時 : " + timeElapsed));
	}
}
