package entpack.enums;

public enum BalanceTypeEnum {
    //accountChange：账户变动类
    accountchange(1),
    //bet：下注扣款
    bet(20),
    //winlose：输赢
    winlose(21);
    private final int value;
    private BalanceTypeEnum(int value) {
        this.value = value;
    }
    public int getValue() {
        return this.value;
    }
}
