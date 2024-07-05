package entpack.tasks;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.json.FastJson;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import entpack.Constant;
import entpack.utils.DateUtil;
import entpack.utils.OkHttpUtil;
import entpack.utils.StringUtil;
import okhttp3.*;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketESTask implements Job {

    private static Logger logger = LoggerFactory.getLogger("ticketES");

    public static final String ticketBackKey = "ticketBack";
    public static final String ticketBackErrorKey = "ticketBackError";

    protected static String esTicketUrl = Constant.getESTicket();

    private static AtomicInteger doItemCount = new AtomicInteger(0);
    private static final int len = 5;
    protected static ExecutorService threadPool = Executors.newFixedThreadPool(len);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        logger.info("开启线程:{} TicketESTask:{} doItemCount:{}",
                doItemCount.get(), DateUtil.formatDate(new Date()), doItemCount.get());

        try {
            if (doItemCount.get() < len) {
                doItemCount.incrementAndGet();

                threadPool.execute(() -> {
                    try {
                        ticketEs();

                    } catch (Exception ex) {
                        ex.printStackTrace();

                    } finally {
                        doItemCount.decrementAndGet();
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            doItemCount.decrementAndGet();
        }

    }

    private void ticketEs() {


        //获取redis原生对象来操作
        Cache redis = Redis.use("ticket");
        String jsonStr = redis.rpop(ticketBackKey);

        while (jsonStr != null) {

            // 写入ES
            try {
                Record ticket = new Record().setColumns(FastJson.getJson().parse(jsonStr, Map.class));

                String createDate = getReportDate(ticket.getStr("createDate"), 11);

                String url = String.format(esTicketUrl + "-" + createDate + "/_doc/%s",
                        ticket.getStr("id"));

                logger.info("esTicketUrl:{}", url);

//                String bettime = ticket.getStr("bettime");
//
//                bettime = bettime.replace(".0", "");
//
//                if (bettime.indexOf("T") > -1) {
//                    Date betDate = DateUtil.getDateFromISO(bettime);
//                    ticket.set("bettime", DateUtil.formatDate(betDate));
//                } else {
//                    ticket.set("bettime", bettime);
//                }
                Response response = postJSON(url, ticket.getColumns());

                response.close();
            } catch (Exception ex) {
                // 保存出错信息
                redis.hset(ticketBackErrorKey, jsonStr,
                        getExceptionToString(ex.fillInStackTrace()));
                ex.printStackTrace();

                logger.error("TicketESTask.error", ex.fillInStackTrace());
            }

            jsonStr = redis.rpop(ticketBackKey);
        }
    }

    /**
     * 获取报表记账时间
     * 转换逻辑: 大于等于12点算当天, 小于算前一天
     *
     * @param dateStr 记录时间
     * @return 返回时间
     */
    public static String getReportDate(String dateStr, int hour) {
        if (StringUtil.toInt(dateStr.substring(11, 13)) >= hour) {
            return dateStr.substring(0, 10);
        } else {
            return DateUtil.formatDate(
                    DateUtils.addDays(DateUtil.parse(dateStr), -1),
                    "yyyy-MM-dd");
        }
    }


    /**
     * 将 Exception 转化为 String
     */
    public static String getExceptionToString(Throwable e) {
        if (e == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    private static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(600, TimeUnit.SECONDS)//设置读取超时时间
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .build();
    }

    /**
     * POST请求
     *
     * @param address
     * @param param
     * @return
     */
    public static Response postJSON(String address, Map<String, Object> param) {
        OkHttpClient client = getClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, JSONObject.toJSONString(param));

        Request.Builder requestBuilder = new Request.Builder()
                .url(address)
                .post(requestBody);


        Request request = requestBuilder.build();
        try {
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
