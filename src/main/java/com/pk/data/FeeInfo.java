package com.pk.data;

/**
 * 
 * 
 * @author Petr Kucera
 *
 */
public class FeeInfo implements Comparable<FeeInfo> {

	float weight;
	float fee;

	public FeeInfo(float weight, float fee) {
		setWeight(weight);
		setFee(fee);
	}
	
	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getFee() {
		return fee;
	}

	public void setFee(float fee) {
		this.fee = fee;
	}

	public int compareTo(FeeInfo other) {
		if (other == null) {
			return 1;
		}
		
		return Math.round(100 * (this.getWeight() - other.getWeight()));
	}

}
