package w.tools;

import w.wexpense.utils.Defaults;

public class UUIDGen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int loop = 10;
		if (args.length > 0) {
			loop = Integer.parseInt(args[0]);
		}
		for(int i=0;i<loop;i++) {
			System.out.println(Defaults.newUid());
		}
	}
}
