package nesto.gankio.network;

/**
 * Created on 2016/4/6.
 * By nesto
 */
public abstract class ErrorHandler {
    /**
     * 当需要额外的处理时，通过传入ErrorHandler，在handleError中进行处理
     * 返回值表示是否需要继续默认处理
     */
    public boolean doBeforeHandle(Throwable throwable) {
        return true;
    }

    /**
     * optional 用来关个dialog什么的
     */
    public void doAfterHandle() {
    }

    /**
     * optional 获取access之后进行重试
     */
    public void retry() {
    }

    /**
     * optional 在某些奇特的情况下处理下404
     */
    public void dealWith404() {
    }
}
