package itu.dd.client.tech.exception;

import android.util.AndroidException;

public class CommunicationException extends AndroidException {
	protected String msg;
	protected String LOGTAG;
	private static final long serialVersionUID = 3286908339345443817L;
	
	public CommunicationException(String LOGTAG, String msg){
		super(LOGTAG + ": "+ msg);
		this.LOGTAG = LOGTAG;
		this.msg = msg;
	}
	
	public CommunicationException(String LOGTAG, String msg, Exception cause){
		super(cause);
		this.LOGTAG = LOGTAG;
		this.msg = msg;
	}
}
