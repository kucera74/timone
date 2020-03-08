package com.pk.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pk.data.FeeInfo;
import com.pk.data.PackageInfo;

public class PackageWorker {

	private final Map<Integer, List<PackageInfo>> packages = new HashMap<Integer, List<PackageInfo>>();
	private final List<FeeInfo> fees = new ArrayList<FeeInfo>();

	public void setFees(List<Float> feesAndWeights) {
		assert feesAndWeights != null : "List of fees has to be set.";
		assert feesAndWeights.size() % 2 == 0 : "Invalid length of fee and weight list";
		fees.clear();

		for (int i = 0; i < feesAndWeights.size();) {
			addFee(feesAndWeights.get(i++), feesAndWeights.get(i++));
		}
	}

	public void addFee(float weight, float fee) {
		FeeInfo newFee = new FeeInfo(weight, fee);

		if (fees.contains(newFee)) {
			System.out.println("\u001B[31mDuplicity for fee [" + weight + "," + fee + "].\u001B[0m");
			return;
		}

		if (fees.size() == 0 || newFee.compareTo(fees.get(fees.size() - 1)) > 0) {
			fees.add(newFee);
		} else if (newFee.compareTo(fees.get(0)) < 0) {
			fees.add(0, newFee);
		} else {
			fees.add(findPosition(0, fees.size() - 1, newFee), newFee);
		}
	}

	/**
	 * Find position of fees which satisfies lowFee < newFee < highFee and highFee
	 * is successor of lowFee in original list of fees.
	 */
	private int findPosition(int low, int high, FeeInfo newFee) {
		if (low + 1 < high) {
			int median = (low+high)/2;
			FeeInfo medFee = fees.get(median);
			if (medFee.compareTo(newFee) < 0) {
				return findPosition(median, high, newFee);
			} else {
				return findPosition(low, median, newFee);
			}
		} else {
			return high;
		}
		
	}

	public void listFees() {
		int size = fees.size();
		if (size == 0) {
			System.out.println("No fee is set.");
		} else {
			System.out.println("|    Weight     | Fee  |");
			System.out.println("+---------------+------+");
			for (int i = 0; i < fees.size() - 1; i++) {
				System.out.printf("|%6.3f - %6.3f|%6.2f|\n", fees.get(i).getWeight(), fees.get(i + 1).getWeight(),
						fees.get(i).getFee());
			}
			System.out.printf("|%6.3f -       |%6.2f|\n", fees.get(size - 1).getWeight(), fees.get(size - 1).getFee());
		}
	}

	public void addPackage(float weight, int zip) {
		assert weight > 0 : "Invalid weight of package (" + weight + "). Weight has to be positive number.";
		assert zip >= 0 && zip <= 99999 : "Invalid postal code. Postal code consists only from 5 digits";

		if (packages.get(zip) == null) {
			packages.put(zip, new ArrayList<PackageInfo>());
		}

		packages.get(zip).add(new PackageInfo(weight, zip, computeFee(weight)));
	}

	public void processPackages() {

	}

	public void printPackageInfo() {
		final List<PackageInfo> toOutput = new ArrayList<PackageInfo>();
		packages.forEach((z, l) -> {
			final PackageInfo pi = new PackageInfo(0.0f, z, 0.0f);
			l.stream().forEach(o -> {
				pi.setFee(pi.getFee() + o.getFee());
				pi.setWeight(pi.getWeight() + o.getWeight());
			});
			toOutput.add(pi);
		});

		toOutput.sort(new Comparator<PackageInfo>() {
			@Override
			public int compare(PackageInfo o1, PackageInfo o2) {
				return o1.compareTo(o2);
			}
		});

		toOutput.forEach(o -> System.out.printf("%05d %.3f %.2f\n", o.getZipCode(), o.getWeight(), o.getFee()));
	}

	private float computeFee(float weight) {
		if (fees.size() == 0) {
			return 0.0f;
		}

		int i = 0;
		FeeInfo result = fees.get(i++);
		while (result.getWeight() < weight && i < fees.size() - 1) {
			result = fees.get(i++);
		}

		return result.getFee();
	}

}
