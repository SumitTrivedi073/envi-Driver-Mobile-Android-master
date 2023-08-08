package sgs.env.ecabsdriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentVerificationResponse {
	
	@SerializedName ("message") @Expose private Message message;
	
	public Message getMessage() {
		return message;
	}
	
	public void setMessage(Message message) {
		this.message = message;
	}
	
	public static class Message {
		
		@SerializedName ("paymentStatus") @Expose private String paymentStatus;
		
		@SerializedName ("txnAmount") @Expose private String txnAmount;
		
		@SerializedName ("resultCode") @Expose private String resultCode;
		
		@SerializedName ("resultMsg") @Expose private String resultMsg;
		
		public String getPaymentStatus() {
			return paymentStatus;
		}
		
		public void setPaymentStatus(String paymentStatus) {
			this.paymentStatus = paymentStatus;
		}
		
		public String getTxnAmount() {
			return txnAmount;
		}
		
		public void setTxnAmount(String txnAmount) {
			this.txnAmount = txnAmount;
		}
		
		public String getResultCode() {
			return resultCode;
		}
		
		public void setResultCode(String resultCode) {
			this.resultCode = resultCode;
		}
		
		public String getResultMsg() {
			return resultMsg;
		}
		
		public void setResultMsg(String resultMsg) {
			this.resultMsg = resultMsg;
		}
		
	}
}
