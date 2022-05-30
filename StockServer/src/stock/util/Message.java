package stock.util;

public class Message {
	private String State;
	
	private String Msg;
	
	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getMsg() {
		return Msg;
	}

	public void setMsg(String msg) {
		Msg = msg;
	}

	private Object Content;
	
	private String Time;

	public Object getContent() {
		return Content;
	}

	public void setContent(Object content) {
		Content = content;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}
}