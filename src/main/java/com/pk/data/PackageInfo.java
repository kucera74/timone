package com.pk.data;

/**
 * Basic package info class, where is stored package weight in kgs and
 * destination postal (zip) code.
 * 
 * @author Petr Kucera
 *
 */
public class PackageInfo implements Comparable<PackageInfo> {

	/** Weight in kilograms */
	private float weight;
	/** Destination postal (zip) code */
	private int zipCode;
	/** Fee for delivery */
	private float fee;
	
	public PackageInfo(float weight, int zipCode) {
		this(weight, zipCode, 0.0f);
	}
	
	public PackageInfo(float weight, int zipCode, float fee) {
		setWeight(weight);
		setZipCode(zipCode);
		setFee(fee);
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public int getZipCode() {
		return zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}

	public float getFee() {
		return fee;
	}

	public void setFee(float fee) {
		this.fee = fee;
	}

	@Override
	public int compareTo(PackageInfo other) {
		if (other == null) {
			return 1;
		}
		
		int z = this.getZipCode() -  other.getZipCode();
		
		int w = Math.round(100 * (this.getWeight() - other.getWeight()));

		return z == 0 ? w : z;
	}

}
