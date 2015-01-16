
public class Test<E> {

	public static void main(String args[]) {
		TreeMapGaun<Integer, String> map2 = new TreeMapGaun<Integer, String>();

		for (int i = 1; i < 11; i++) {
			map2.put(i, ""+i+"");
		}
		
		System.err.println(map2);
	}
	@SuppressWarnings("unused")
	private static long startTimer() {
		return System.currentTimeMillis();
	}

	@SuppressWarnings("unused")
	private static void endTimer(String methodName, long startTime) {
		long endTime = System.currentTimeMillis();
		System.out.println(methodName + "execution_time: "
				+ (endTime - startTime) + " milliSeconds");
	}
}
