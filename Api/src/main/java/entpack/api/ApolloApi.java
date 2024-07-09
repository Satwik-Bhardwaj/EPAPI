package entpack.api;

import entpack.Constant;

import java.util.HashMap;
import java.util.Map;

public class ApolloApi extends ApolloBaseApi {

    private static Map<String, ApolloApi> apiMap = new HashMap<>();

    private final static Map<String, String> agentIdMap = new HashMap<>();

    static {
        String def_Api = "apollo";
        String open_currency = Constant.GameConfig.get(def_Api + ".open_currency");
        //初始化api
        for (String currency : open_currency.split(",")) {
            String currency_config = Constant.GameConfig.get(def_Api + ".currency." + currency);
            for (String config : currency_config.split("\\|")) {
                String[] configs = config.split(",");

                String agentId = configs[0];
                String dc = configs[1];
                String secretKey = configs[2];
                String iv = configs[3];
                apiMap.put(currency, new ApolloApi(currency, iv, secretKey, agentId, dc));

                if (currency.equals("HKD")) {
                    apiMap.put("HK", new ApolloApi(currency, iv, secretKey, agentId, dc));
                }
                // TODO : please check iv is placed correctly
                agentIdMap.put(agentId, iv);
            }
        }
    }

    public ApolloApi(String currency, String iv, String secretKey, String agentId, String dc) {
        this.currency = currency;
        this.api_agent = agentId;
        this.iv = iv;
        this.secretKey = secretKey;
        this.dc = dc;
    }
    public static ApolloApi getInstance(String currency) {
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
    public String getIv() {
        return iv;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public String getApiAgent() {
        return api_agent;
    }

    public static Map<String, ApolloApi> getApiMap() {
        return apiMap;
    }
}
