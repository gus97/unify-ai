package cn.migu.unify.ai.utils.http;

public class StopVo {

    private long id;
    private long num;
    private long time;

    public StopVo(long id, long num, long time) {
        this.id = id;
        this.num = num;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
