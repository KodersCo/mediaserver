package org.mobicents.media.server.impl.rtcp;

/**
 * Represents an abstraction of an RTCP Report.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 * 
 */
public abstract class RtcpReport extends RtcpHeader {

	/**
	 * Source that generated the report
	 */
	protected long ssrc;

	/**
	 * Reports coming from other sync sources
	 */
	protected RtcpReceiverReportItem[] receiverReports;
	
	protected RtcpReport() {
		this.receiverReports = new RtcpReceiverReportItem[RtcpPacket.MAX_SOURCES];
	}

	protected RtcpReport(boolean padding, long ssrc, int packetType) {
		super(padding, packetType);
		this.ssrc = ssrc;
		this.receiverReports = new RtcpReceiverReportItem[RtcpPacket.MAX_SOURCES];
	}

	/**
	 * Tells whether this reports was generated by a sender or a receiver.
	 * 
	 * @return Whether this is a Sender Report or not.
	 */
	public abstract boolean isSender();
	
	public long getSsrc() {
		return this.ssrc;
	}
	
	public RtcpReceiverReportItem[] getReceiverReports() {
		return this.receiverReports;
	}

	public void addReceiverReport(RtcpReceiverReportItem rtcpReceptionReportItem) {
		this.receiverReports[this.count++] = rtcpReceptionReportItem;
	}
}
