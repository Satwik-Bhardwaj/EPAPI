package entpack.enums;

public enum RechargeStatusEnum {
    //-1：deleted-已删除，0：wait-待处理，1：lock-取单，4：cancel-取消，8：finish-完成
    deleted(-1),
    wait(0),
    lock(1),
    cancel(4),
    finish(8);

    private final int value;
    private RechargeStatusEnum(int value) {
        this.value = value;
    }
    public int getValue() {
        return this.value;
    }
}
