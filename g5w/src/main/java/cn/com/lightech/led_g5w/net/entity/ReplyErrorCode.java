package cn.com.lightech.led_g5w.net.entity;

/**单片机返回数据的错误代码*/
public enum ReplyErrorCode {

	OK(0),//返回正确

	/**包长度有错*/
	NotEnoughData(1),

	/**包头错误*/
	HeaderError(2),

	/**包标识的长度错误*/
	DataLengthError(3),

	/**校验码错误*/
	ValidateCodeError(4),

	/**超时*/
	TimeOut(5),

	/**单片机处理错误*/
	LogicError(6),

	/**未知错误*/
	UnKnow(7),

	/**ID格式错误*/
	IDFormatError(8),

	/**数据包校验和失败*/
	ValidateSumFailed(9);

	private int value;
	private ReplyErrorCode(int val)
	{
		this.value =val;
	}

	public int toInt()
	{
		return this.value;
	}
}
