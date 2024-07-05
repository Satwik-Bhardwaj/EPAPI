package entpack.api;

import entpack.Constant;

import java.util.HashMap;
import java.util.Map;

public class Kiss918Api extends Kiss918BaseApi {

    private static Map<String, Kiss918Api> apiMap = new HashMap<>();

    private final static Map<String, String> agentIdMap = new HashMap<>();

    static {
        String def_Api = "kiss918";
        String open_currency = Constant.GameConfig.getEnv(def_Api + "_open_currency");
        //初始化api
        for (String currency : open_currency.split(",")) {
            String currency_config = Constant.GameConfig.getEnv(def_Api + "_currency_" + currency);
            for (String config : currency_config.split("\\|")) {
                String[] configs = config.split(",");

                String authCode = configs[0];
                String secretKey = configs[1];
                String agent = configs[2];
                apiMap.put(currency, new Kiss918Api(currency, authCode, secretKey, agent));

                if (currency.equals("HKD")) {
                    apiMap.put("HK", new Kiss918Api(currency, authCode, secretKey, agent));
                }
                agentIdMap.put(agent, authCode);
            }
        }
    }

    public Kiss918Api(String currency, String authCode, String secretKey, String agent) {
        this.currency = currency;
        this.api_agent = agent;
        this.authCode = authCode;
        this.secretKey = secretKey;
    }
    public static Kiss918Api getInstance(String currency) {
        return apiMap.get(currency);
    }

    @Override
    public String getApi() {
        switch (currency) {
            case "HK":
            case "HKD":
            case "THB":
            case "MMK":
            case "PHP":
            case "JPY":
            case "SGD":
            default:
                return def_Api + currency.toLowerCase();
            case "MYR":
                return def_Api;
        }
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public String getApiAgent() {
        return api_agent;
    }

    public static Map<String, Kiss918Api> getApiMap() {
        return apiMap;
    }
}
