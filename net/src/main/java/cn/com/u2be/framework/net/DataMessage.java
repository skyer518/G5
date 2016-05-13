package cn.com.u2be.framework.net;

public class DataMessage {
    private ConnMsgType msgType;
    private ConnState connState;
    private String text;
    private byte[] byteArray;
    //private Response response;

    public ConnMsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(ConnMsgType msgType) {
        this.msgType = msgType;
    }

    public ConnState getConnState() {
        return connState;
    }

    public void setConnState(ConnState connState) {
        this.connState = connState;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }
    /*public Response getResponse() {
        return response;
	}
	public void setResponse(Response response) {
		this.response = response;
	}*/
}
