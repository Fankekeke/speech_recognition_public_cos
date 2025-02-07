package cc.mrbird.febs.cos.analysis;

public interface TaskCallBack {

    void onComplete(Object msg);

    void onFail(String error);

}
