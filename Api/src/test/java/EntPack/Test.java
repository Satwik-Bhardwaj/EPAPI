package EntPack;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
    private static Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {

//		Date betTime = DateUtil.getDateFromISO("2023-11-13T19:07:40.963+08:00" );
//		Date betTime1 = DateUtil.getDateFromISO("2023-11-11T12:17:15.9400000+08:00".replace("0000","") ,"yyyy-MM-dd'T'HH:mm:ss.SSSX");

        DateTimeFormatter isoformat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        DateTime dt = isoformat.parseDateTime("2023-11-19T07:57:39.9671452");
        System.out.println(dt.toDate());
        System.out.println(dt.toString(isoformat));
//
//		//2021-03-26T12+08:00
//		//2023-01-08T13+08:00
//		String startTime = DateUtil.formatDateISO(
//				DateUtil.addHour(new Date(), Calendar.MINUTE, -5),
//				"yyyy-MM-dd'T'hhXXX");
//		System.out.println(startTime);
//
//		double userBalance = (double) 10005000.7500;
//		System.out.println("awcApi.deposit2Game userBalance:"+ userBalance);
//		if (userBalance > 0) {
//			System.out.println("userBalance > 0");
//
//			long amount = (long) (userBalance);
//
//			DecimalFormat df = new java.text.DecimalFormat("#0.00");
//
//
//			if (amount > 0) {
//				System.out.println("amount "+df.format(amount));
//
//			}
//		}

//		long amount = (long)583.19;
//		Double depotAmount = (double) amount;
//		depotAmount = depotAmount / 2;
//		System.out.println(depotAmount);
//
//		DecimalFormat	df = new DecimalFormat("#0");
//		String TranAmountVal = df.format(depotAmount.longValue());
//		System.out.println(TranAmountVal);

    }
}
