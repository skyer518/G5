package cn.com.lightech.led_g5g.presenter.responsibility;

/**
 * Created by alek on 2016/5/17.
 */
public abstract class DutyHandler {

    private DutyHandler successor;

    public DutyHandler getSuccessor() {
        return successor;
    }

    public void setSuccessor(DutyHandler successor) {
        this.successor = successor;
    }

    public abstract void handleRequest(RequestEntity request);

    public DutyHandler(DutyHandler successor) {
        this.successor = successor;
    }

    public DutyHandler() {
    }

    public void handNext(final RequestEntity request) {
        if (getSuccessor() != null) {
            getSuccessor().handleRequest(request);
        }
    }
}