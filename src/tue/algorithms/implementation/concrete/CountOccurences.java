package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import tue.algorithms.other.Pair;

public class CountOccurences {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		HashMap<String, Integer> occurences = new HashMap<String, Integer>();
		int num = 0;
		float Tfloat1 = 0;
		float Tfloat2 = 0;
		float Tfloat3 = 0;
		float Tfloat4 = 0;
		while (true) {
			String line = scanner.nextLine();
			if (line.equalsIgnoreCase("exit")) {
				break;
			}
			if (!line.isEmpty()) {
				if (occurences.containsKey(line)) {
					occurences.put(line, occurences.get(line)+1);
				} else {
					occurences.put(line, 1);
				}
				try {
					String[] split = line.split(" ");
					float float1 = Float.parseFloat(split[0]);
					float float2 = Float.parseFloat(split[1]);
					float float3 = Float.parseFloat(split[2]);
					float float4 = Float.parseFloat(split[3]);
					Tfloat1 += float1;
					Tfloat2 += float2;
					Tfloat3 += float3;
					Tfloat4 += float4;
					num++;
				} catch (Exception e) {}
			}
		}
		scanner.close();
		ArrayList<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>();
		for (Entry<String, Integer> entry : occurences.entrySet()) {
			list.add(new Pair<String, Integer>(entry.getKey(), entry.getValue()));
		}
		Collections.sort(list, new Comparator<Pair<String, Integer>>() {
			
			@Override
			public int compare(Pair<String, Integer> arg0,
					Pair<String, Integer> arg1) {
				int occurence0 = arg0.second();
				int occurence1 = arg1.second();
				if (occurence0 < occurence1) {
					return 1;
				} else if (occurence0 > occurence1) {
					return -1;
				}
				return 0;
			}
			
		});
		System.out.println("--- Top occurences ---");
		for (Pair<String, Integer> pair : list) {
			System.out.println(pair.second() + " : " + pair.first());
		}
		System.out.println("--- Average occurence ---");
		System.out.println(Tfloat1/num + " " + Tfloat2/num + " " + Tfloat3/num + " " + Tfloat4/num);
	}
	
}
