package entpack.bean;

public class GameInfoEx extends GameInfo {
    //平台游戏Id
    private String pfGameId;

    public String getPfGameId(){
        return pfGameId;
    }

    public void setPfGameId(String pfGameId) {
        this.pfGameId = pfGameId;
    }
}
