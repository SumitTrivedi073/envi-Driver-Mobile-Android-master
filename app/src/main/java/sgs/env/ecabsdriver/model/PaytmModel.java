package sgs.env.ecabsdriver.model;

public class PaytmModel {
	
	private String passengerTripMasterId;
	
	public String getPassengerTripMasterId() {
		return passengerTripMasterId;
	}
	
	public void setPassengerTripMasterId(String passengerTripMasterId) {
		this.passengerTripMasterId = passengerTripMasterId;
	}

	@Override
	public String toString() {
		return "PaytmModel{" +
				"passengerTripMasterId='" + passengerTripMasterId + '\'' +
				'}';
	}
}
